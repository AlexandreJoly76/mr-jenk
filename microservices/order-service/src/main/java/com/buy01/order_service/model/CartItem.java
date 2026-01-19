package com.buy01.order_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private String productId;
    private String productName;
    private double price;
    private int quantity;
    private String imageUrl; // Utile pour l'affichage frontend
}