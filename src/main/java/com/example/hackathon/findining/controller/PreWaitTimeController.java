package com.example.hackathon.findining.controller;

import com.example.hackathon.findining.service.PreWaitTimeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class PreWaitTimeController {
    private final PreWaitTimeService preWaitTimeService;

    @PostMapping("/predict")
    public ResponseEntity<?> getTodayPredictedWaitTime() {
        List<Map<String, Object>> result = preWaitTimeService.getOrGenerateTodayPredictions();
        return ResponseEntity.ok(result);
    }
}
