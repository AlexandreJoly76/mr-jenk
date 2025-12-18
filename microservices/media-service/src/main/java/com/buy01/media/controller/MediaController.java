package com.buy01.media.controller;

import com.buy01.media.model.Media;
import com.buy01.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    // POST : Upload d'image
    // On utilise "consumes = MediaType.MULTIPART_FORM_DATA_VALUE" car ce n'est pas du JSON
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Media upload(@RequestParam("file") MultipartFile file) throws IOException {
        return mediaService.uploadImage(file);
    }

    // --- AJOUT : Endpoint pour afficher l'image ---
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable String id) {
        Media media = mediaService.getMedia(id);
        String inused = "debug";
        System.out.println("hello c'est la controller media");

        return ResponseEntity.ok()
                // On dit au navigateur : "Ceci est une image (jpeg, png...)"
                .contentType(MediaType.valueOf(media.getContentType()))
                // On envoie les données brutes
                .body(media.getData());
    }
}