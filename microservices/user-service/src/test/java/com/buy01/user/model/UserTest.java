package com.buy01.user.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserBuilderAndGetters() {
        User user = User.builder()
                .id("1")
                .name("John Doe")
                .email("john@example.com")
                .password("password123")
                .role(Role.CLIENT)
                .avatar("avatar.png")
                .build();

        assertEquals("1", user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(Role.CLIENT, user.getRole());
        assertEquals("avatar.png", user.getAvatar());
    }

    @Test
    void testUserSetters() {
        User user = new User();
        user.setId("2");
        user.setName("Jane Doe");
        user.setEmail("jane@example.com");
        user.setPassword("securePass");
        user.setRole(Role.SELLER);
        user.setAvatar("jane.png");

        assertEquals("2", user.getId());
        assertEquals("Jane Doe", user.getName());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("securePass", user.getPassword());
        assertEquals(Role.SELLER, user.getRole());
        assertEquals("jane.png", user.getAvatar());
    }

    @Test
    void testNoArgsConstructor() {
        User user = new User();
        assertNull(user.getId());
        assertNull(user.getName());
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User("3", "Admin", "admin@example.com", "admin123", Role.SELLER, "admin.png");
        assertEquals("3", user.getId());
        assertEquals("Admin", user.getName());
        assertEquals(Role.SELLER, user.getRole());
    }
}
