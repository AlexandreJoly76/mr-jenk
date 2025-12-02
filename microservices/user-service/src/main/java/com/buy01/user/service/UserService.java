package com.buy01.user.service;

import com.buy01.user.model.User;
import com.buy01.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Notre outil de hachage injecté

    public User registerUser(User user) {
        // Règle de sécurité : On crypte le mot de passe avant de sauvegarder
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // On ajoutera la méthode loginUser plus tard
}