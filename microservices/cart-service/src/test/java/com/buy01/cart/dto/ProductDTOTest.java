package com.buy01.cart.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProductDTOTest {

    @Test
    void testProductDTOData() {
        BigDecimal price = new BigDecimal("100.00");
        ProductDTO dto = new ProductDTO();
        dto.setId("p1");
        dto.setName("Product 1");
        dto.setPrice(price);
        dto.setQuantity(10);
        dto.setUserId("user1");

        assertEquals("p1", dto.getId());
        assertEquals("Product 1", dto.getName());
        assertEquals(price, dto.getPrice());
        assertEquals(10, dto.getQuantity());
        assertEquals("user1", dto.getUserId());

        ProductDTO dto2 = new ProductDTO();
        dto2.setId("p1");
        dto2.setName("Product 1");
        dto2.setPrice(price);
        dto2.setQuantity(10);
        dto2.setUserId("user1");

        assertEquals(dto, dto2);
        assertEquals(dto.hashCode(), dto2.hashCode());
        assertNotNull(dto.toString());
    }
}
