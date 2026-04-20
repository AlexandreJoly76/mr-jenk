package com.buy01.cart.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    @Test
    void testCartItemData() {
        BigDecimal price = new BigDecimal("10.00");
        CartItem item = CartItem.builder()
                .productId("p1")
                .productName("Product 1")
                .price(price)
                .quantity(2)
                .sellerId("s1")
                .build();

        assertEquals("p1", item.getProductId());
        assertEquals("Product 1", item.getProductName());
        assertEquals(price, item.getPrice());
        assertEquals(2, item.getQuantity());
        assertEquals("s1", item.getSellerId());

        item.setQuantity(5);
        assertEquals(5, item.getQuantity());

        CartItem item2 = new CartItem("p1", "Product 1", price, 5, "s1");
        assertEquals(item, item2);
        assertEquals(item.hashCode(), item2.hashCode());
        assertNotNull(item.toString());
    }

    @Test
    void testNoArgsConstructor() {
        CartItem item = new CartItem();
        assertNotNull(item);
    }
}
