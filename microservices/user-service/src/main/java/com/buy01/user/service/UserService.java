package com.buy01.user.service;

import com.buy01.user.model.User;
import com.buy01.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Notre outil de hachage injecté
    private final JwtService jwtService; // <--- Injection du JwtService

    public User registerUser(User user) {
        // Règle de sécurité : On crypte le mot de passe avant de sauvegarder
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // --- NOUVELLE MÉTHODE : LOGIN ---
    public String loginUser(String email, String password) {
        // 1. Chercher l'utilisateur par email (c'est plus unique que le nom)
        // Note: Tu devras peut-être ajouter findByEmail dans UserRepository si ce n'est pas fait !
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 2. Vérifier si le mot de passe en clair correspond au hash en BDD
            if (passwordEncoder.matches(password, user.getPassword())) {
                // 3. C'est gagné : on génère le token
                return jwtService.generateToken(user);
            } else {
                throw new RuntimeException("Mot de passe incorrect");
            }
        } else {
            throw new RuntimeException("Utilisateur non trouvé");
        }
    }
}