package com.example.hackathon.order.repository;

import com.example.hackathon.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Integer> {
    Optional<Order> findByUserId(String userId);
}
