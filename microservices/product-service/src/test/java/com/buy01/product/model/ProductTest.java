package com.buy01.product.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testProductGettersAndSetters() {
        Product product = new Product();
        product.setId("1");
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setCategory("Test Category");
        product.setPrice(new BigDecimal("100.00"));
        product.setUserId("user1");
        List<String> images = Arrays.asList("img1", "img2");
        product.setImageIds(images);
        product.setQuantity(10);
        product.setSellerName("Seller One");

        assertEquals("1", product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("Test Description", product.getDescription());
        assertEquals("Test Category", product.getCategory());
        assertEquals(new BigDecimal("100.00"), product.getPrice());
        assertEquals("user1", product.getUserId());
        assertEquals(images, product.getImageIds());
        assertEquals(10, product.getQuantity());
        assertEquals("Seller One", product.getSellerName());
    }

    @Test
    void testProductBuilder() {
        List<String> images = Arrays.asList("img1", "img2");
        Product product = Product.builder()
                .id("1")
                .name("Test Product")
                .description("Test Description")
                .category("Test Category")
                .price(new BigDecimal("100.00"))
                .userId("user1")
                .imageIds(images)
                .quantity(10)
                .SellerName("Seller One")
                .build();

        assertEquals("1", product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("Test Description", product.getDescription());
        assertEquals("Test Category", product.getCategory());
        assertEquals(new BigDecimal("100.00"), product.getPrice());
        assertEquals("user1", product.getUserId());
        assertEquals(images, product.getImageIds());
        assertEquals(10, product.getQuantity());
        assertEquals("Seller One", product.getSellerName());
    }

    @Test
    void testNoArgsConstructor() {
        Product product = new Product();
        assertNotNull(product);
    }

    @Test
    void testAllArgsConstructor() {
        List<String> images = Arrays.asList("img1", "img2");
        Product product = new Product("1", "Test Product", "Test Description", "Test Category", 
                new BigDecimal("100.00"), "user1", images, 10, "Seller One");
        
        assertEquals("1", product.getId());
        assertEquals("Test Product", product.getName());
    }
}
