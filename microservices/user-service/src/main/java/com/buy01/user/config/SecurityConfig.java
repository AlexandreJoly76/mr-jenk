package com.buy01.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. L'outil pour hacher les mots de passe
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Les règles du pare-feu
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Désactive CSRF (inutile pour les API Stateless comme nous)
                .csrf(AbstractHttpConfigurer::disable)
                // Autorise tout le monde à accéder à l'inscription et au login (qu'on fera plus tard)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/**").permitAll() // On laisse passer les requêtes vers /api/users
                        .anyRequest().authenticated() // Tout le reste nécessite d'être connecté
                );

        return http.build();
    }
}