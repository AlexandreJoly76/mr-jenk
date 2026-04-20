package com.buy01.cart.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleRuntimeException_ReturnsBadRequest() {
        RuntimeException ex = new RuntimeException("Test error message");
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleRuntimeException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test error message", response.getBody().get("error"));
    }
}
