package com.buy01.order.service;

import com.buy01.order.dto.*;
import com.buy01.order.model.Order;
import com.buy01.order.model.OrderItem;
import com.buy01.order.model.OrderStatus;
import com.buy01.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private OrderProducer orderProducer;

    @InjectMocks
    private OrderService orderService;

    private WebClient webClient;
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        webClient = mock(WebClient.class);
        requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        lenient().when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    void searchOrders() {
        when(mongoTemplate.count(any(Query.class), eq(Order.class))).thenReturn(1L);
        when(mongoTemplate.find(any(Query.class), eq(Order.class))).thenReturn(Collections.singletonList(new Order()));

        Page<Order> result = orderService.searchOrders("u1", null, null, null, null, null, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void cancelOrder_Success() {
        Order order = Order.builder().userId("u1").status(OrderStatus.PENDING).build();
        when(orderRepository.findById("o1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order result = orderService.cancelOrder("u1", "o1");

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
    }

    @Test
    void cancelOrder_NotFound() {
        when(orderRepository.findById("o1")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> orderService.cancelOrder("u1", "o1"));
    }

    @Test
    void cancelOrder_WrongUser() {
        Order order = Order.builder().userId("u2").build();
        when(orderRepository.findById("o1")).thenReturn(Optional.of(order));
        assertThrows(RuntimeException.class, () -> orderService.cancelOrder("u1", "o1"));
    }

    @Test
    void cancelOrder_NotPending() {
        Order order = Order.builder().userId("u1").status(OrderStatus.PAID).build();
        when(orderRepository.findById("o1")).thenReturn(Optional.of(order));
        assertThrows(RuntimeException.class, () -> orderService.cancelOrder("u1", "o1"));
    }

    @Test
    void deleteOrder_Success() {
        Order order = Order.builder().userId("u1").status(OrderStatus.DELIVERED).build();
        when(orderRepository.findById("o1")).thenReturn(Optional.of(order));

        orderService.deleteOrder("u1", "o1");

        verify(orderRepository).deleteById("o1");
    }

    @Test
    void updateOrderStatus_Success() {
        OrderItem item = OrderItem.builder().sellerId("s1").productId("p1").quantity(1).build();
        Order order = Order.builder().items(Collections.singletonList(item)).status(OrderStatus.PENDING).build();
        when(orderRepository.findById("o1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order result = orderService.updateOrderStatus("s1", "o1", OrderStatus.PAID);

        assertEquals(OrderStatus.PAID, result.getStatus());
        verify(orderProducer).sendStockUpdate("p1", 1);
    }

    @Test
    void redoOrder() {
        Order oldOrder = Order.builder()
                .userId("u1")
                .items(new ArrayList<>())
                .totalAmount(BigDecimal.TEN)
                .paymentMethod("CARD")
                .shippingAddress("Addr")
                .build();
        when(orderRepository.findById("o1")).thenReturn(Optional.of(oldOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order result = orderService.redoOrder("u1", "o1", "token");

        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(BigDecimal.TEN, result.getTotalAmount());
    }

    @Test
    void checkout() {
        CartDTO cart = new CartDTO();
        cart.setUserId("u1");
        CartItemDTO item = new CartItemDTO();
        item.setProductId("p1");
        item.setPrice(BigDecimal.TEN);
        item.setQuantity(2);
        item.setSellerId("s1");
        cart.setItems(Collections.singletonList(item));

        // Mock WebClient chain for fetchCart
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CartDTO.class)).thenReturn(Mono.just(cart));

        // Mock WebClient chain for clearCart
        WebClient.RequestHeadersUriSpec deleteSpec = mock(WebClient.RequestHeadersUriSpec.class);
        when(webClient.delete()).thenReturn(deleteSpec);
        when(deleteSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        // requestHeadersSpec already mocked above
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        CheckoutRequest request = new CheckoutRequest();
        request.setPaymentMethod("CARD");
        request.setShippingAddress("Addr");

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order result = orderService.checkout("u1", request, "token");

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(20), result.getTotalAmount());
        assertEquals(OrderStatus.PENDING, result.getStatus());
    }
}
