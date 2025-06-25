package com.example.hackathon.menu.service;

import com.example.hackathon.menu.domain.Menu;
import com.example.hackathon.menu.dto.MenuResponse;
import com.example.hackathon.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    public List<MenuResponse> getMenusByLocation(int location) {
        List<Menu> menus = menuRepository.findByLocation(location);
        return menus.stream()
                .map(MenuResponse::from)
                .collect(Collectors.toList());
    }
}
