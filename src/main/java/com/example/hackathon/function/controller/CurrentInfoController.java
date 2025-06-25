package com.example.hackathon.function.controller;

import com.example.hackathon.function.dto.CurrentInfoResponse;
import com.example.hackathon.function.service.CurrentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CurrentInfoController {

    private final CurrentInfoService currentInfoService;

    @GetMapping("/currentwait")
    public CurrentInfoResponse getCurrentWaitTime() {
        return currentInfoService.getEstimatedWaitTime();
    }
}
