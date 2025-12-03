package com.buy01.product.repository;

import com.buy01.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    // La magie Spring : findBy + NomDuChamp
    List<Product> findByUserId(String userId);
}