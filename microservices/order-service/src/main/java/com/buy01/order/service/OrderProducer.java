package com.buy01.order.service;

import com.buy01.order.dto.StockUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendStockUpdate(String productId, Integer quantity) {
        log.info("Envoi mise à jour stock pour le produit {} : -{}", productId, quantity);
        kafkaTemplate.send("stock-update", new StockUpdateEvent(productId, quantity));
    }
}
