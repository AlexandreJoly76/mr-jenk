package com.buy01.order.repository;

import com.buy01.order.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface OrderRepository extends MongoRepository<Order, String> {
    Page<Order> findByUserId(String userId, Pageable pageable);
    
    @Query("{ 'items.sellerId': ?0 }")
    Page<Order> findBySellerId(String sellerId, Pageable pageable);
}
