package com.example.hackathon.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="user_id", nullable = false, length = 50)
    private String userId;
    @Column(name="user_name", nullable = false, length = 50)
    private String useName;
    @Column(name="order_count", nullable = false)
    private int orderCount;

    public void increaseOrderCount() {
        this.orderCount++;
    }

    public static Order createNew(String userId, String useName) {
        return new Order(null, userId, useName, 1);
    }

}
