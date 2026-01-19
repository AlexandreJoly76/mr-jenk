package com.buy01.order_service.service;

import com.buy01.order_service.model.Cart;
import com.buy01.order_service.model.CartItem;
import com.buy01.order_service.repository.CartRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    // Récupérer le panier d'un user (ou en créer un vide)
    public Cart getCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    cart.setTotalPrice(0.0);
                    return cartRepository.save(cart);
                });
    }

    // Ajouter un item au panier
    public Cart addToCart(String userId, CartItem item) {
        Cart cart = getCart(userId);

        // Vérifier si le produit est déjà dans le panier
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(item.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // On met à jour la quantité
            CartItem found = existingItem.get();
            found.setQuantity(found.getQuantity() + item.getQuantity());
        } else {
            // On ajoute le nouvel item
            cart.getItems().add(item);
        }

        recalculateTotal(cart);
        return cartRepository.save(cart);
    }

    // Supprimer un item
    public Cart removeFromCart(String userId, String productId) {
        Cart cart = getCart(userId);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        recalculateTotal(cart);
        return cartRepository.save(cart);
    }

    // Vider le panier (après commande par exemple)
    public void clearCart(String userId) {
        Cart cart = getCart(userId);
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);
    }

    // Méthode utilitaire pour recalculer le prix
    private void recalculateTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        cart.setTotalPrice(total);
    }
}