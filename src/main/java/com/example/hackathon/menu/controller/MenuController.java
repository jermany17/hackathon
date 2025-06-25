package com.example.hackathon.menu.controller;

import com.example.hackathon.menu.dto.MenuResponse;
import com.example.hackathon.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @GetMapping("/menu")
    public ResponseEntity<?> menu(@RequestParam int location) {
        List<MenuResponse> menus = menuService.getMenusByLocation(location);
        return ResponseEntity.ok(menus);
    }
}
