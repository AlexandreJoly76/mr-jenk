package com.buy01.user.controller;

import com.buy01.user.model.User;
import com.buy01.user.service.UserService;
import jakarta.validation.Valid;
import lombok.Data; // Import Lombok
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody @Valid User user) {
        return userService.registerUser(user);
    }

    // --- LOGIN ---
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        return userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
    }
}

// Petite classe DTO pour recevoir les infos proprement
@Data
class LoginRequest {
    private String email;
    private String password;
}