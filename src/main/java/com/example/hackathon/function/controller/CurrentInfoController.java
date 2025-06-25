package com.example.hackathon.function.controller;

import com.example.hackathon.function.dto.CurrentInfoResponse;
import com.example.hackathon.function.service.CurrentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CurrentInfoController {

    private final CurrentInfoService currentInfoService;

    @GetMapping("/currentwait")
    public CurrentInfoResponse getCurrentWaitTime(
            @RequestParam("location") int location,
            @RequestParam("weekday") int weekday
    ) {
        return currentInfoService.getEstimatedWaitTime(location, weekday);
    }
}
