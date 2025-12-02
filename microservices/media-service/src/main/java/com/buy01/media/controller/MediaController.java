package com.buy01.media.controller;

import com.buy01.media.model.Media;
import com.buy01.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
}