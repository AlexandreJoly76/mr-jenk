package com.buy01.media.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaConsumerTest {

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private MediaConsumer mediaConsumer;

    @Test
    void consumeImageDeletion_Success() {
        String imageId = "test-id";
        
        mediaConsumer.consumeImageDeletion(imageId);
        
        verify(mediaService, times(1)).deleteImage(imageId);
    }
}
