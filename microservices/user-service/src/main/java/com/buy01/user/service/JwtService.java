package com.buy01.user.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    // ⚠️ TRES IMPORTANT : Ceci est une clé secrète. En prod, elle doit être dans application.yml !
    // Elle doit être longue (256 bits min) pour l'algo HS256.
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    // MODIFICATION ICI : On passe l'objet User entier
    public String generateToken(com.buy01.user.model.User user) {
        Map<String, Object> claims = new HashMap<>();
        // On ajoute l'ID et le Role dans le token !
        claims.put("id", user.getId());
        claims.put("role", user.getRole());
        claims.put("avatar", user.getAvatar());

        return createToken(claims, user.getName());
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName) // Le "sujet" du token est le nom d'utilisateur
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Valide 10 heures
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // On signe numériquement
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}