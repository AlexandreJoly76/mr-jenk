package com.buy01.media.service;

import com.buy01.media.model.Media;
import com.buy01.media.repository.MediaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private MediaRepository mediaRepository;

    @InjectMocks
    private MediaService mediaService;

    private Media sampleMedia;

    @BeforeEach
    void setUp() {
        sampleMedia = Media.builder()
                .id("1")
                .name("test.jpg")
                .contentType("image/jpeg")
                .data("test data".getBytes())
                .build();
    }

    @Test
    void uploadImage_Success() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test data".getBytes());

        when(mediaRepository.save(any(Media.class))).thenReturn(sampleMedia);

        Media result = mediaService.uploadImage(file);

        assertNotNull(result);
        assertEquals("test.jpg", result.getName());
        verify(mediaRepository, times(1)).save(any(Media.class));
    }

    @Test
    void uploadImage_NullContentType() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", null, "test data".getBytes());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mediaService.uploadImage(file);
        });

        assertEquals("Format invalide : Seules les images sont acceptées !", exception.getMessage());
        verify(mediaRepository, never()).save(any(Media.class));
    }

    @Test
    void uploadImage_InvalidFormat() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "test data".getBytes());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mediaService.uploadImage(file);
        });

        assertEquals("Format invalide : Seules les images sont acceptées !", exception.getMessage());
        verify(mediaRepository, never()).save(any(Media.class));
    }

    @Test
    void getMedia_Success() {
        when(mediaRepository.findById("1")).thenReturn(Optional.of(sampleMedia));

        Media result = mediaService.getMedia("1");

        assertNotNull(result);
        assertEquals("1", result.getId());
        verify(mediaRepository, times(1)).findById("1");
    }

    @Test
    void getMedia_NotFound() {
        when(mediaRepository.findById("1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mediaService.getMedia("1");
        });

        assertEquals("Image non trouvée avec l'id : 1", exception.getMessage());
        verify(mediaRepository, times(1)).findById("1");
    }

    @Test
    void deleteImage_Success() {
        doNothing().when(mediaRepository).deleteById("1");

        mediaService.deleteImage("1");

        verify(mediaRepository, times(1)).deleteById("1");
    }
}
