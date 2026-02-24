package com.buy01.order.controller;

import com.buy01.order.dto.CheckoutRequest;
import com.buy01.order.dto.SellerStatsDTO;
import com.buy01.order.dto.UserStatsDTO;
import com.buy01.order.model.Order;
import com.buy01.order.model.OrderStatus;
import com.buy01.order.service.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Base64;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getClaim(String token, String claim) {
        try {
            // Remove "Bearer " prefix if present
            String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
            String payload = jwt.split("\\.")[1];
            String decodedPayload = new String(Base64.getDecoder().decode(payload));
            JsonNode json = objectMapper.readTree(decodedPayload);
            return json.get(claim).asText();
        } catch (Exception e) {
            throw new RuntimeException("Token invalide : " + e.getMessage());
        }
    }

    @GetMapping("/stats/user")
    public UserStatsDTO getMyStats(@RequestHeader("Authorization") String token) {
        String userId = getClaim(token, "id");
        return orderService.getUserStats(userId);
    }

    @GetMapping("/stats/seller")
    public SellerStatsDTO getSellerStats(@RequestHeader("Authorization") String token) {
        String role = getClaim(token, "role");
        if (!"SELLER".equals(role)) {
            throw new RuntimeException("Accès réservé aux vendeurs");
        }
        String sellerId = getClaim(token, "id");
        return orderService.getSellerStats(sellerId);
    }

    @GetMapping
    public Page<Order> getMyOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String userId = getClaim(token, "id");
        return orderService.searchOrders(userId, null, status, start, end, keyword, PageRequest.of(page, size));
    }

    @GetMapping("/seller")
    public Page<Order> getSellerOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String role = getClaim(token, "role");
        if (!"SELLER".equals(role)) {
            throw new RuntimeException("Accès réservé aux vendeurs");
        }
        String sellerId = getClaim(token, "id");
        return orderService.searchOrders(null, sellerId, status, start, end, keyword, PageRequest.of(page, size));
    }

    @PostMapping("/{id}/cancel")
    public Order cancelOrder(@RequestHeader("Authorization") String token, @PathVariable String id) {
        String userId = getClaim(token, "id");
        return orderService.cancelOrder(userId, id);
    }

    @PutMapping("/{id}/status")
    public Order updateOrderStatus(
            @RequestHeader("Authorization") String token, 
            @PathVariable String id, 
            @RequestParam OrderStatus status) {
        String role = getClaim(token, "role");
        if (!"SELLER".equals(role)) {
            throw new RuntimeException("Accès réservé aux vendeurs");
        }
        String sellerId = getClaim(token, "id");
        return orderService.updateOrderStatus(sellerId, id, status);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@RequestHeader("Authorization") String token, @PathVariable String id) {
        String userId = getClaim(token, "id");
        orderService.deleteOrder(userId, id);
    }

    @PostMapping("/{id}/redo")
    public Order redoOrder(@RequestHeader("Authorization") String token, @PathVariable String id) {
        String userId = getClaim(token, "id");
        return orderService.redoOrder(userId, id, token);
    }

    @PostMapping("/checkout")
    public Order checkout(@RequestHeader("Authorization") String token, @RequestBody @Valid CheckoutRequest request) {
        String userId = getClaim(token, "id");
        return orderService.checkout(userId, request, token);
    }
}
