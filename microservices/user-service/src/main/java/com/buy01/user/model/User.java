package com.buy01.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data // Lombok: Génère Getters, Setters, toString...
@AllArgsConstructor // Lombok: Constructeur avec tous les arguments
@NoArgsConstructor // Lombok: Constructeur vide
@Builder // Lombok: Permet de créer des objets de façon fluide
@Document(collection = "users") // MongoDB: Cette classe correspond à la collection "users"
public class User {

    @Id // La clé unique (générée par Mongo)
    private String id;
    @NotBlank(message = "Le nom est obligatoire")
    @Indexed(unique=true)
    private String name;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Indexed(unique = true) // Tu l'avais déjà mis
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit faire au moins 6 caractères")
    private String password;
    private Role role;
    private String avatar;
}