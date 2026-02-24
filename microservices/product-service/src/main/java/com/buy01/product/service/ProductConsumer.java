package com.buy01.product.service;

import com.buy01.order.dto.StockUpdateEvent;
import com.buy01.product.model.Product;
import com.buy01.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductConsumer {

    private final ProductRepository productRepository;

    @KafkaListener(topics = "stock-update", groupId = "product-group")
    public void consumeStockUpdate(StockUpdateEvent event) {
        log.info("Réception mise à jour stock : Produit {}, Quantité -{}", event.getProductId(), event.getQuantity());
        
        productRepository.findById(event.getProductId()).ifPresent(product -> {
            int newQuantity = product.getQuantity() - event.getQuantity();
            if (newQuantity < 0) newQuantity = 0;
            product.setQuantity(newQuantity);
            productRepository.save(product);
            log.info("Stock mis à jour pour {}. Nouveau stock : {}", product.getName(), newQuantity);
        });
    }
}
