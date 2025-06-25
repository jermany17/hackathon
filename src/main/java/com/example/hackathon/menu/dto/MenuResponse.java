package com.example.hackathon.menu.dto;

import com.example.hackathon.menu.domain.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenuResponse {
    private int id;
    private String menuName;
    private int menuPrice;

    public static MenuResponse from(Menu menu) {
        return new MenuResponse(menu.getId(), menu.getMenuName(), menu.getMenuPrice());
    }
}
