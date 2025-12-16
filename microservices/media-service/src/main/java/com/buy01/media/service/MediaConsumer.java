package com.buy01.media.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaConsumer {

    private final MediaService mediaService;

    // Cette méthode se déclenche automatiquement dès qu'un message arrive sur "image-deletion"
    @KafkaListener(topics = "image-deletion", groupId = "media-group")
    public void consumeImageDeletion(String imageId) {
        log.info("Message Kafka reçu : Suppression de l'image {}", imageId);
        mediaService.deleteImage(imageId);
    }
}