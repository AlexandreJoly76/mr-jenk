package com.buy01.media.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "medias")
public class Media {
    @Id
    private String id;
    private String name;       // Nom du fichier (ex: photo.jpg)
    private String contentType; // Type (ex: image/jpeg)
    private byte[] data;       // Le contenu binaire de l'image
}