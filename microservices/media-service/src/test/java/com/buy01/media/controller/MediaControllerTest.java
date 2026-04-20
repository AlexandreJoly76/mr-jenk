package com.buy01.media.controller;

import com.buy01.media.model.Media;
import com.buy01.media.service.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MediaController.class)
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
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
    void upload_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test data".getBytes());

        when(mediaService.uploadImage(any())).thenReturn(sampleMedia);

        mockMvc.perform(multipart("/api/media")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("test.jpg"));

        verify(mediaService, times(1)).uploadImage(any());
    }

    @Test
    void getImage_Success() throws Exception {
        when(mediaService.getMedia("1")).thenReturn(sampleMedia);

        mockMvc.perform(get("/api/media/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes("test data".getBytes()));

        verify(mediaService, times(1)).getMedia("1");
    }
}
