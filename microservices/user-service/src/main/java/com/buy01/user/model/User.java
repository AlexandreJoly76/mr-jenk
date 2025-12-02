package com.buy01.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data // Lombok: Génère Getters, Setters, toString...
@AllArgsConstructor // Lombok: Constructeur avec tous les arguments
@NoArgsConstructor // Lombok: Constructeur vide
@Builder // Lombok: Permet de créer des objets de façon fluide
@Document(collection = "users") // MongoDB: Cette classe correspond à la collection "users"
public class User {

    @Id // La clé unique (générée par Mongo)
    private String id;
    private String username;
    private String email;
    // On ajoutera le mot de passe plus tard
}