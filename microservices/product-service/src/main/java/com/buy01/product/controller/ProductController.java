package com.buy01.product.controller;

import com.buy01.product.model.Product;
import com.buy01.product.repository.ProductRepository;
import com.buy01.product.service.ProductProducer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductProducer productProducer;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
    }

    @GetMapping("/search")
    public Page<Product> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "false") boolean availableOnly,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Query query = new Query().with(PageRequest.of(page, size));
        List<Criteria> criteriaList = new ArrayList<>();

        if (keyword != null && !keyword.isEmpty()) {
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("name").regex(keyword, "i"),
                    Criteria.where("description").regex(keyword, "i")
            ));
        }

        if (category != null && !category.isEmpty()) {
            criteriaList.add(Criteria.where("category").is(category));
        }

        if (minPrice != null || maxPrice != null) {
            Criteria priceCriteria = Criteria.where("price");
            if (minPrice != null) priceCriteria.gte(minPrice);
            if (maxPrice != null) priceCriteria.lte(maxPrice);
            criteriaList.add(priceCriteria);
        }

        if (availableOnly) {
            criteriaList.add(Criteria.where("quantity").gt(0));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        query.with(sort);

        long count = mongoTemplate.count(query, Product.class);
        List<Product> products = mongoTemplate.find(query, Product.class);

        return new PageImpl<>(products, PageRequest.of(page, size), count);
    }

    private void verifySellerRole(String token) {
        try {
            String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
            String payload = jwt.split("\\.")[1];
            String decodedPayload = new String(Base64.getDecoder().decode(payload));
            JsonNode json = objectMapper.readTree(decodedPayload);
            String role = json.get("role").asText();
            if (!"SELLER".equals(role)) {
                throw new RuntimeException("Accès refusé");
            }
        } catch (Exception e) {
            throw new RuntimeException("Token invalide");
        }
    }

    @PostMapping
    public Product createProduct(@RequestBody @Valid Product product, @RequestHeader("Authorization") String token) {
        verifySellerRole(token);
        return productRepository.save(product);
    }

    @GetMapping("/seller/{userId}")
    public List<Product> getProductsBySeller(@PathVariable String userId) {
        return productRepository.findByUserId(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id, @RequestHeader("Authorization") String token) {
        verifySellerRole(token);
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            if (product.getImageIds() != null) {
                for (String imageId : product.getImageIds()) {
                    productProducer.sendImageDeletionRequest(imageId);
                }
            }
            productRepository.deleteById(id);
        }
    }
}
