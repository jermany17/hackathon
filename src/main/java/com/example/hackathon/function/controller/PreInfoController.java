package com.example.hackathon.function.controller;

import com.example.hackathon.function.dto.PreInfoResponse;
import com.example.hackathon.function.service.PreInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/preinfo")
@RequiredArgsConstructor
public class PreInfoController {

    private final PreInfoService preInfoService;

    @GetMapping("/{location}")
    public List<PreInfoResponse> getPreInfoByLocation(@PathVariable Integer location) {
        return preInfoService.getPreInfoByLocation(location);
    }
}
