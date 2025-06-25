package com.example.hackathon.menu.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private int location;
    @Column(name="menu_name", nullable = false, length = 50)
    private String menuName;
    @Column(name="menu_price", nullable = false)
    private int menuPrice;
}
