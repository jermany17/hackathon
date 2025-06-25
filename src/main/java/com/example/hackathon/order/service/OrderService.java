package com.example.hackathon.order.service;

import com.example.hackathon.order.domain.Order;
import com.example.hackathon.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void recordOrder(String userId, String useName) {
        Optional<Order> optional = orderRepository.findByUserId(userId);
        if (optional.isPresent()) {
            Order order = optional.get();
            order.increaseOrderCount();
            orderRepository.save(order);
        } else {
            Order newOrder = Order.createNew(userId, useName);
            orderRepository.save(newOrder);
        }
    }
}
