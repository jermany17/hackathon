package com.example.hackathon.findining.controller;

import com.example.hackathon.findining.dto.CurrentInfoResponse;
import com.example.hackathon.findining.dto.RecommendSaturationResponse;
import com.example.hackathon.findining.dto.RecommendTimeResponse;
import com.example.hackathon.findining.service.CurrentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/recommend_time")
    public RecommendTimeResponse recommendTime(@RequestParam("weekday") int weekday) {
        return currentInfoService.recommendLocation(weekday);
    }

    @GetMapping("/recommend_saturation")
    public RecommendSaturationResponse recommendSaturation(@RequestParam("weekday") int weekday) {
        return currentInfoService.recommendSaturation(weekday);
    }
}
