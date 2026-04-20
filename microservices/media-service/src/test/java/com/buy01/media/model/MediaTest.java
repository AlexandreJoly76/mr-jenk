package com.buy01.media.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MediaTest {

    @Test
    void testMediaBuilderAndGetters() {
        byte[] data = "test data".getBytes();
        Media media = Media.builder()
                .id("1")
                .name("test.jpg")
                .contentType("image/jpeg")
                .data(data)
                .build();

        assertEquals("1", media.getId());
        assertEquals("test.jpg", media.getName());
        assertEquals("image/jpeg", media.getContentType());
        assertArrayEquals(data, media.getData());
    }

    @Test
    void testMediaNoArgsConstructor() {
        Media media = new Media();
        assertNull(media.getId());
        assertNull(media.getName());
        assertNull(media.getContentType());
        assertNull(media.getData());
    }

    @Test
    void testMediaAllArgsConstructor() {
        byte[] data = "test data".getBytes();
        Media media = new Media("1", "test.jpg", "image/jpeg", data);
        assertEquals("1", media.getId());
        assertEquals("test.jpg", media.getName());
        assertEquals("image/jpeg", media.getContentType());
        assertArrayEquals(data, media.getData());
    }

    @Test
    void testMediaSetter() {
        Media media = new Media();
        media.setId("1");
        media.setName("test.jpg");
        media.setContentType("image/jpeg");
        byte[] data = "test data".getBytes();
        media.setData(data);

        assertEquals("1", media.getId());
        assertEquals("test.jpg", media.getName());
        assertEquals("image/jpeg", media.getContentType());
        assertArrayEquals(data, media.getData());
    }

    @Test
    void testEqualsAndHashCode() {
        byte[] data = "test data".getBytes();
        Media media1 = new Media("1", "test.jpg", "image/jpeg", data);
        Media media2 = new Media("1", "test.jpg", "image/jpeg", data);

        assertEquals(media1, media2);
        assertEquals(media1.hashCode(), media2.hashCode());
    }

    @Test
    void testToString() {
        Media media = new Media("1", "test.jpg", "image/jpeg", null);
        assertNotNull(media.toString());
    }
}
