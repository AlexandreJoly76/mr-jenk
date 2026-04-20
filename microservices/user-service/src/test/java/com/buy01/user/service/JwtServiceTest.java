package com.buy01.user.service;

import com.buy01.user.model.Role;
import com.buy01.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Key;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void testGenerateToken() {
        User user = User.builder()
                .id("123")
                .name("testuser")
                .role(Role.CLIENT)
                .avatar("avatar.png")
                .build();

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Validate token content
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("testuser", claims.getSubject());
        assertEquals("123", claims.get("id"));
        assertEquals("CLIENT", claims.get("role"));
        assertEquals("avatar.png", claims.get("avatar"));
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JwtService.SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
