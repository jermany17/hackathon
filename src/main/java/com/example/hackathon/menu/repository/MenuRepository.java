package com.example.hackathon.menu.repository;

import com.example.hackathon.menu.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Integer> {

    List<Menu> findByLocation(int location);
}
