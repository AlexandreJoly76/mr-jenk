package com.buy01.product.controller;

import com.buy01.product.model.Product;
import com.buy01.product.repository.ProductRepository;
import com.buy01.product.service.ProductProducer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductProducer productProducer; // Injection du Producer
    private final ObjectMapper objectMapper = new ObjectMapper(); // Pour lire le JSON


    // Méthode utilitaire pour vérifier le rôle SELLER
    private void verifySellerRole(String token) {
        try {
            // Le token arrive sous forme "Bearer eyJ..."
            String payload = token.split("\\.")[1]; // On prend la partie du milieu
            String decodedPayload = new String(Base64.getDecoder().decode(payload));
            JsonNode json = objectMapper.readTree(decodedPayload);

            String role = json.get("role").asText();

            if (!"SELLER".equals(role)) {
                throw new RuntimeException("Accès refusé : Seuls les vendeurs peuvent faire ça.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Token invalide ou erreur de lecture rôle"); // Simplifié pour l'audit
        }
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product, @RequestHeader("Authorization") String token) {
        verifySellerRole(token);
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
    public void deleteProduct(@PathVariable String id, @RequestHeader("Authorization") String token) {
        verifySellerRole(token);
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