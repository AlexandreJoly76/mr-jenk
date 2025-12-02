package com.buy01.user.controller;

import com.buy01.user.model.User;
import com.buy01.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor // Lombok: Injecte automatiquement le repository (Constructeur)
public class TestController {

    private final UserRepository userRepository;

    // POST: Créer un utilisateur test
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    // GET: Lister tous les utilisateurs
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}