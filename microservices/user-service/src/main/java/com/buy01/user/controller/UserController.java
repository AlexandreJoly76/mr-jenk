package com.buy01.user.controller;

import com.buy01.user.model.User;
import com.buy01.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Inscription
    @PostMapping("/register") // Changement léger de route : /api/users/register
    public User register(@RequestBody User user) {
        return userService.registerUser(user);
    }
}