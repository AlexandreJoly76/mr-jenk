package com.buy01.product.controller;

import com.buy01.product.model.Product;
import com.buy01.product.repository.ProductRepository;
import com.buy01.product.service.ProductProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductProducer productProducer; // Injection du Producer

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // --- AJOUT 1 : Récupérer les produits d'un vendeur spécifique ---
    @GetMapping("/seller/{userId}")
    public List<Product> getProductsBySeller(@PathVariable String userId) {
        return productRepository.findByUserId(userId);
    }

    // --- AJOUT 2 : Supprimer un produit ---
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        // 1. On cherche le produit AVANT de le supprimer pour avoir l'ID de l'image
        Product product = productRepository.findById(id).orElse(null);

        if (product != null) {
            // 2. Si le produit a une image, on prévient Kafka
            if (product.getImageId() != null) {
                productProducer.sendImageDeletionRequest(product.getImageId());
            }
            // 3. On supprime le produit
            productRepository.deleteById(id);
        }
    }
}