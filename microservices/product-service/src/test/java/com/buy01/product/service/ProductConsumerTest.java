package com.buy01.product.service;

import com.buy01.order.dto.StockUpdateEvent;
import com.buy01.product.model.Product;
import com.buy01.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

class ProductConsumerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductConsumer productConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void consumeStockUpdate_ShouldUpdateProductQuantity() {
        StockUpdateEvent event = new StockUpdateEvent("prod1", 2);
        Product product = new Product();
        product.setId("prod1");
        product.setQuantity(10);
        product.setName("Test Product");

        when(productRepository.findById("prod1")).thenReturn(Optional.of(product));

        productConsumer.consumeStockUpdate(event);

        verify(productRepository).save(argThat(p -> p.getQuantity() == 8));
    }

    @Test
    void consumeStockUpdate_ShouldNotAllowNegativeQuantity() {
        StockUpdateEvent event = new StockUpdateEvent("prod1", 15);
        Product product = new Product();
        product.setId("prod1");
        product.setQuantity(10);
        product.setName("Test Product");

        when(productRepository.findById("prod1")).thenReturn(Optional.of(product));

        productConsumer.consumeStockUpdate(event);

        verify(productRepository).save(argThat(p -> p.getQuantity() == 0));
    }

    @Test
    void consumeStockUpdate_ShouldDoNothing_WhenProductNotFound() {
        StockUpdateEvent event = new StockUpdateEvent("prod1", 2);
        when(productRepository.findById("prod1")).thenReturn(Optional.empty());

        productConsumer.consumeStockUpdate(event);

        verify(productRepository, never()).save(any(Product.class));
    }
}
