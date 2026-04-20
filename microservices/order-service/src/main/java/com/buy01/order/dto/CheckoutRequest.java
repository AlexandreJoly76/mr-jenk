package com.buy01.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotBlank(message = "L'adresse de livraison est requise")
    private String shippingAddress;
    
    @NotBlank(message = "La méthode de paiement est requise")
    private String paymentMethod;
}
