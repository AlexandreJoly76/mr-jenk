package com.buy01.media.service;

import com.buy01.media.model.Media;
import com.buy01.media.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;

    public Media uploadImage(MultipartFile file) throws IOException {
        // Validation basique (Spring vérifie déjà la taille via l'application.yml)
        return mediaRepository.save(Media.builder()
                .name(file.getOriginalFilename())
                .contentType(file.getContentType())
                .data(file.getBytes()) // Convertit le fichier en tableau de bytes
                .build());
    }
}