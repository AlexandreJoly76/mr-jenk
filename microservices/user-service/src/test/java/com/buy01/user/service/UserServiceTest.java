package com.buy01.user.service;

import com.buy01.user.model.User;
import com.buy01.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("1")
                .name("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
    }

    @Test
    void testRegisterUser() {
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User registered = userService.registerUser(testUser);

        assertNotNull(registered);
        assertEquals("hashedPassword", testUser.getPassword());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testLoginUser_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("mockToken");

        String token = userService.loginUser("test@example.com", "password123");

        assertEquals("mockToken", token);
    }

    @Test
    void testLoginUser_UserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.loginUser("nonexistent@example.com", "password"));
    }

    @Test
    void testLoginUser_IncorrectPassword() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.loginUser("test@example.com", "wrongPassword"));
    }
}
