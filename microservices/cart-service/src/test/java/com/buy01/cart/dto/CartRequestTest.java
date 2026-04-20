package com.buy01.cart.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartRequestTest {

    @Test
    void testCartRequestData() {
        CartRequest request = new CartRequest();
        request.setProductId("p1");
        request.setQuantity(2);

        assertEquals("p1", request.getProductId());
        assertEquals(2, request.getQuantity());

        CartRequest request2 = new CartRequest();
        request2.setProductId("p1");
        request2.setQuantity(2);

        assertEquals(request, request2);
        assertEquals(request.hashCode(), request2.hashCode());
        assertNotNull(request.toString());
    }
}
