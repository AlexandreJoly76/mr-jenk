package com.buy01.cart.controller;

import com.buy01.cart.dto.CartRequest;
import com.buy01.cart.model.Cart;
import com.buy01.cart.service.CartService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Méthode utilitaire pour extraire l'ID utilisateur du token
    private String extractUserId(String token) {
        try {
            // Remove "Bearer " prefix if present
            String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
            String payload = jwt.split("\\.")[1];
            String decodedPayload = new String(Base64.getDecoder().decode(payload));
            JsonNode json = objectMapper.readTree(decodedPayload);
            return json.get("id").asText();
        } catch (Exception e) {
            throw new RuntimeException("Token invalide ou erreur d'authentification");
        }
    }

    @GetMapping
    public Cart getCart(@RequestHeader("Authorization") String token) {
        String userId = extractUserId(token);
        return cartService.getCartByUserId(userId);
    }

    @PostMapping
    public Cart addToCart(@RequestHeader("Authorization") String token, @RequestBody @Valid CartRequest request) {
        String userId = extractUserId(token);
        return cartService.addToCart(userId, request);
    }

    @PutMapping
    public Cart updateQuantity(@RequestHeader("Authorization") String token, @RequestBody @Valid CartRequest request) {
        String userId = extractUserId(token);
        return cartService.updateQuantity(userId, request);
    }

    @DeleteMapping("/{productId}")
    public Cart removeFromCart(@RequestHeader("Authorization") String token, @PathVariable String productId) {
        String userId = extractUserId(token);
        return cartService.removeFromCart(userId, productId);
    }

    @DeleteMapping("/clear")
    public void clearCart(@RequestHeader("Authorization") String token) {
        String userId = extractUserId(token);
        cartService.clearCart(userId);
    }
}
