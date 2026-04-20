package com.buy01.cart.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    @Test
    void testCartData() {
        List<CartItem> items = new ArrayList<>();
        Cart cart = Cart.builder()
                .id("1")
                .userId("user123")
                .items(items)
                .build();

        assertEquals("1", cart.getId());
        assertEquals("user123", cart.getUserId());
        assertEquals(items, cart.getItems());

        cart.setId("2");
        assertEquals("2", cart.getId());

        Cart cart2 = new Cart("2", "user123", items);
        assertEquals(cart, cart2);
        assertEquals(cart.hashCode(), cart2.hashCode());
        assertNotNull(cart.toString());
    }

    @Test
    void testNoArgsConstructor() {
        Cart cart = new Cart();
        assertNotNull(cart);
        assertNotNull(cart.getItems());
        assertTrue(cart.getItems().isEmpty());
    }
}
