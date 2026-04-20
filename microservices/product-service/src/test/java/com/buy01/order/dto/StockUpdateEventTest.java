package com.buy01.order.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StockUpdateEventTest {

    @Test
    void testGettersAndSetters() {
        StockUpdateEvent event = new StockUpdateEvent();
        event.setProductId("prod1");
        event.setQuantity(5);

        assertEquals("prod1", event.getProductId());
        assertEquals(5, event.getQuantity());
    }

    @Test
    void testAllArgsConstructor() {
        StockUpdateEvent event = new StockUpdateEvent("prod1", 5);
        assertEquals("prod1", event.getProductId());
        assertEquals(5, event.getQuantity());
    }

    @Test
    void testNoArgsConstructor() {
        StockUpdateEvent event = new StockUpdateEvent();
        assertNotNull(event);
    }
}
