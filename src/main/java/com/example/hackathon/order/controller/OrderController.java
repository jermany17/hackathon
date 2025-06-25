package com.example.hackathon.order.controller;

import com.example.hackathon.auth.domain.User;
import com.example.hackathon.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity<?> order(Authentication authentication) {
        User user = (User) authentication.getPrincipal(); // 로그인 유저 정보
        String userId = user.getUserId();
        String useName = user.getUsername(); // 또는 getName()

        orderService.recordOrder(userId, useName);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "주문완료");
        return ResponseEntity.ok(response);
    }
}
