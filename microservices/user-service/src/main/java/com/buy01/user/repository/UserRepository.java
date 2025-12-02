package com.buy01.user.repository;

import com.buy01.user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

// <User, String> signifie : Je gère des objets "User" dont l'ID est de type "String"
public interface UserRepository extends MongoRepository<User, String> {
}