package com.buy01.order.controller;

import com.buy01.order.dto.CheckoutRequest;
import com.buy01.order.model.Order;
import com.buy01.order.model.OrderStatus;
import com.buy01.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private String createToken(String id, String role) {
        String payload = "{\"id\":\"" + id + "\", \"role\":\"" + role + "\"}";
        String encodedPayload = Base64.getEncoder().encodeToString(payload.getBytes());
        return "header." + encodedPayload + ".signature";
    }

    @Test
    void getMyStats() throws Exception {
        String token = "Bearer " + createToken("u1", "USER");
        mockMvc.perform(get("/api/orders/stats/user")
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void getSellerStats_Success() throws Exception {
        String token = "Bearer " + createToken("s1", "SELLER");
        mockMvc.perform(get("/api/orders/stats/seller")
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void getSellerStats_Forbidden() throws Exception {
        String token = "Bearer " + createToken("u1", "USER");
        mockMvc.perform(get("/api/orders/stats/seller")
                .header("Authorization", token))
                .andExpect(status().isBadRequest()); // Controller throws RuntimeException
    }

    @Test
    void getMyOrders() throws Exception {
        String token = "Bearer " + createToken("u1", "USER");
        when(orderService.searchOrders(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/orders")
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void getSellerOrders() throws Exception {
        String token = "Bearer " + createToken("s1", "SELLER");
        when(orderService.searchOrders(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/orders/seller")
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void cancelOrder() throws Exception {
        String token = "Bearer " + createToken("u1", "USER");
        when(orderService.cancelOrder(anyString(), anyString())).thenReturn(new Order());

        mockMvc.perform(post("/api/orders/o1/cancel")
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void updateOrderStatus() throws Exception {
        String token = "Bearer " + createToken("s1", "SELLER");
        when(orderService.updateOrderStatus(anyString(), anyString(), any())).thenReturn(new Order());

        mockMvc.perform(put("/api/orders/o1/status")
                .header("Authorization", token)
                .param("status", "PAID"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteOrder() throws Exception {
        String token = "Bearer " + createToken("u1", "USER");
        mockMvc.perform(delete("/api/orders/o1")
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void redoOrder() throws Exception {
        String token = "Bearer " + createToken("u1", "USER");
        when(orderService.redoOrder(anyString(), anyString(), anyString())).thenReturn(new Order());

        mockMvc.perform(post("/api/orders/o1/redo")
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void checkout() throws Exception {
        String token = "Bearer " + createToken("u1", "USER");
        CheckoutRequest request = new CheckoutRequest();
        request.setPaymentMethod("CARD");
        request.setShippingAddress("Addr");

        when(orderService.checkout(anyString(), any(), anyString())).thenReturn(new Order());

        mockMvc.perform(post("/api/orders/checkout")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
