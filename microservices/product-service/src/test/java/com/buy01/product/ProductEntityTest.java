package com.buy01.product;

import com.buy01.product.model.Product;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProductEntityTest {

    @Test
    void shouldCreateProductObject() {
        // 1. On crée un produit vide
        Product p = new Product();

        // 2. On remplit les données
        p.setId("123");
        p.setName("iPhone Test");
        p.setPrice(BigDecimal.valueOf(999));

        // 3. On vérifie que Java a bien stocké les infos (Test unitaire)
        assertEquals("123", p.getId());
        assertEquals("iPhone Test", p.getName());
        assertEquals(BigDecimal.valueOf(999), p.getPrice());

        System.out.println("✅ TEST UNITAIRE PRODUIT RÉUSSI");
    }
}