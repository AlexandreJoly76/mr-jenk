package com.buy01.product.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    
    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(min = 3, message = "Le nom doit faire au moins 3 caractères")
    @Indexed // Index for keyword search performance
    private String name;
    
    @NotBlank(message = "La description est obligatoire")
    private String description;
    
    @NotBlank(message = "La catégorie est obligatoire")
    @Indexed // Index for category filtering
    private String category;
    
    @NotNull(message = "Le prix est obligatoire")
    @Min(value = 0, message = "Le prix ne peut pas être négatif")
    @Indexed // Index for price range filtering and sorting
    private BigDecimal price;
    
    @NotBlank(message = "L'ID utilisateur est requis")
    private String userId; // Lien vers le User Service (on stocke juste l'ID)
    
    private List<String> imageIds;
    
    @Min(value = 0, message = "La quantité ne peut pas être négative")
    @Indexed // Index for availability filtering
    private Integer quantity;

    private String SellerName;
}