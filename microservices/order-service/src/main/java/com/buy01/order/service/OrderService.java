package com.buy01.order.service;

import com.buy01.order.dto.CartDTO;
import com.buy01.order.dto.CheckoutRequest;
import com.buy01.order.model.Order;
import com.buy01.order.model.OrderItem;
import com.buy01.order.model.OrderStatus;
import com.buy01.order.dto.*;
import com.buy01.order.model.Order;
import com.buy01.order.model.OrderStatus;
import com.buy01.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final MongoTemplate mongoTemplate;

    public UserStatsDTO getUserStats(String userId) {
        // 1. Total spent & total orders
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userId").is(userId)),
                Aggregation.group("userId")
                        .sum("totalAmount").as("totalSpent")
                        .count().as("totalOrders")
        );
        AggregationResults<Map> results = mongoTemplate.aggregate(agg, "orders", Map.class);
        Map baseStats = results.getUniqueMappedResult();

        // 2. Top Products
        Aggregation topProdAgg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userId").is(userId)),
                Aggregation.unwind("items"),
                Aggregation.group("items.productId")
                        .first("items.productName").as("productName")
                        .count().as("count"),
                Aggregation.sort(Sort.Direction.DESC, "count"),
                Aggregation.limit(5)
        );
        List<ProductSummaryDTO> topProducts = mongoTemplate.aggregate(topProdAgg, "orders", ProductSummaryDTO.class).getMappedResults();

        BigDecimal spent = (baseStats != null && baseStats.get("totalSpent") != null) 
                ? new BigDecimal(baseStats.get("totalSpent").toString()) : BigDecimal.ZERO;
        long count = (baseStats != null && baseStats.get("totalOrders") != null) 
                ? Long.parseLong(baseStats.get("totalOrders").toString()) : 0;

        return UserStatsDTO.builder()
                .totalSpent(spent)
                .totalOrders(count)
                .topProducts(topProducts)
                .build();
    }

    public SellerStatsDTO getSellerStats(String sellerId) {
        // 1. Revenue & Completed Orders
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.unwind("items"),
                Aggregation.match(Criteria.where("items.sellerId").is(sellerId)),
                Aggregation.group("_id") // Regrouper par commande d'abord pour compter les commandes uniques
                        .first("status").as("status")
                        .sum("items.priceAtPurchase").as("orderRevenue"),
                Aggregation.match(Criteria.where("status").is(OrderStatus.DELIVERED)),
                Aggregation.group()
                        .sum("orderRevenue").as("totalRevenue")
                        .count().as("completedOrders")
        );
        AggregationResults<Map> results = mongoTemplate.aggregate(agg, "orders", Map.class);
        Map baseStats = results.getUniqueMappedResult();

        // 2. Best Sellers
        Aggregation bestSellersAgg = Aggregation.newAggregation(
                Aggregation.unwind("items"),
                Aggregation.match(Criteria.where("items.sellerId").is(sellerId)),
                Aggregation.group("items.productId")
                        .first("items.productName").as("productName")
                        .count().as("count"),
                Aggregation.sort(Sort.Direction.DESC, "count"),
                Aggregation.limit(5)
        );
        List<ProductSummaryDTO> bestSellers = mongoTemplate.aggregate(bestSellersAgg, "orders", ProductSummaryDTO.class).getMappedResults();

        BigDecimal revenue = (baseStats != null && baseStats.get("totalRevenue") != null) 
                ? new BigDecimal(baseStats.get("totalRevenue").toString()) : BigDecimal.ZERO;
        long completed = (baseStats != null && baseStats.get("completedOrders") != null) 
                ? Long.parseLong(baseStats.get("completedOrders").toString()) : 0;

        return SellerStatsDTO.builder()
                .totalRevenue(revenue)
                .completedOrders(completed)
                .bestSellers(bestSellers)
                .build();
    }

    public Page<Order> searchOrders(String userId, String sellerId, OrderStatus status, 
                                   LocalDateTime start, LocalDateTime end, String keyword, Pageable pageable) {
        Query query = new Query().with(pageable);
        List<Criteria> criteriaList = new ArrayList<>();

        if (userId != null) criteriaList.add(Criteria.where("userId").is(userId));
        if (sellerId != null) criteriaList.add(Criteria.where("items.sellerId").is(sellerId));
        if (status != null) criteriaList.add(Criteria.where("status").is(status));
        if (start != null && end != null) criteriaList.add(Criteria.where("createdAt").gte(start).lte(end));
        if (keyword != null) criteriaList.add(Criteria.where("items.productName").regex(keyword, "i"));

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        long count = mongoTemplate.count(query, Order.class);
        List<Order> orders = mongoTemplate.find(query, Order.class);

        return new PageImpl<>(orders, pageable, count);
    }

    public Order cancelOrder(String userId, String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Accès refusé");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Seules les commandes en attente peuvent être annulées");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public void deleteOrder(String userId, String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Accès refusé");
        }

        if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.CANCELLED) {
            throw new RuntimeException("Impossible de supprimer une commande active");
        }

        orderRepository.deleteById(orderId);
    }

    public Order updateOrderStatus(String sellerId, String orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        // Vérifier si le vendeur a au moins un produit dans cette commande
        boolean isSellerOfOrder = order.getItems().stream()
                .anyMatch(item -> item.getSellerId().equals(sellerId));

        if (!isSellerOfOrder) {
            throw new RuntimeException("Accès refusé : Vous n'êtes pas le vendeur de cette commande");
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Transactional
    public Order redoOrder(String userId, String orderId, String token) {
        Order oldOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        if (!oldOrder.getUserId().equals(userId)) {
            throw new RuntimeException("Accès refusé");
        }

        Order newOrder = Order.builder()
                .userId(userId)
                .items(oldOrder.getItems())
                .totalAmount(oldOrder.getTotalAmount())
                .status(OrderStatus.PENDING)
                .paymentMethod(oldOrder.getPaymentMethod())
                .shippingAddress(oldOrder.getShippingAddress())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return orderRepository.save(newOrder);
    }

    @Transactional
    public Order checkout(String userId, CheckoutRequest request, String token) {
        // 1. Récupérer le panier
        CartDTO cart = fetchCart(token);
        
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Le panier est vide");
        }

        // 2. Calculer le montant total
        BigDecimal totalAmount = cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Créer l'objet Order
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(item -> OrderItem.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .priceAtPurchase(item.getPrice())
                        .quantity(item.getQuantity())
                        .sellerId(item.getSellerId())
                        .build())
                .collect(Collectors.toList());

        Order order = Order.builder()
                .userId(userId)
                .items(orderItems)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .shippingAddress(request.getShippingAddress())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 4. Sauvegarder la commande
        Order savedOrder = orderRepository.save(order);

        // 5. Vider le panier
        clearCart(token);

        return savedOrder;
    }

    private CartDTO fetchCart(String token) {
        return webClientBuilder.build()
                .get()
                .uri("http://cart-service/api/carts")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(CartDTO.class)
                .block();
    }

    private void clearCart(String token) {
        webClientBuilder.build()
                .delete()
                .uri("http://cart-service/api/carts/clear")
                .header("Authorization", token)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
