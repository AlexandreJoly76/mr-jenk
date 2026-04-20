package com.buy01.cart.controller;

import com.buy01.cart.dto.CartRequest;
import com.buy01.cart.model.Cart;
import com.buy01.cart.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Base64;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    private String validToken;
    private String userId = "user123";

    @BeforeEach
    void setUp() {
        String payload = "{\"id\":\"" + userId + "\"}";
        String encodedPayload = Base64.getEncoder().encodeToString(payload.getBytes());
        validToken = "Bearer header." + encodedPayload + ".signature";
    }

    @Test
    void getCart_Success() throws Exception {
        Cart cart = Cart.builder().userId(userId).items(new ArrayList<>()).build();
        when(cartService.getCartByUserId(userId)).thenReturn(cart);

        mockMvc.perform(get("/api/carts")
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void addToCart_Success() throws Exception {
        CartRequest request = new CartRequest();
        request.setProductId("p1");
        request.setQuantity(2);

        Cart cart = Cart.builder().userId(userId).items(new ArrayList<>()).build();
        when(cartService.addToCart(eq(userId), any(CartRequest.class))).thenReturn(cart);

        mockMvc.perform(post("/api/carts")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void updateQuantity_Success() throws Exception {
        CartRequest request = new CartRequest();
        request.setProductId("p1");
        request.setQuantity(5);

        Cart cart = Cart.builder().userId(userId).items(new ArrayList<>()).build();
        when(cartService.updateQuantity(eq(userId), any(CartRequest.class))).thenReturn(cart);

        mockMvc.perform(put("/api/carts")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void removeFromCart_Success() throws Exception {
        Cart cart = Cart.builder().userId(userId).items(new ArrayList<>()).build();
        when(cartService.removeFromCart(userId, "p1")).thenReturn(cart);

        mockMvc.perform(delete("/api/carts/p1")
                .header("Authorization", validToken))
                .andExpect(status().isOk());
    }

    @Test
    void clearCart_Success() throws Exception {
        mockMvc.perform(delete("/api/carts/clear")
                .header("Authorization", validToken))
                .andExpect(status().isOk());
    }

    @Test
    void testInvalidToken() throws Exception {
        mockMvc.perform(get("/api/carts")
                .header("Authorization", "Bearer invalid.token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Token invalide ou erreur d'authentification"));
    }
}
