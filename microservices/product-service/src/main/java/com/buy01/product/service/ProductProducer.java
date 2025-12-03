package com.buy01.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j // Pour les logs
public class ProductProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendImageDeletionRequest(String imageId) {
        log.info("Envoi demande suppression image : {}", imageId);
        // On envoie l'ID de l'image dans le "topic" (canal) nommé "image-deletion"
        kafkaTemplate.send("image-deletion", imageId);
    }
}