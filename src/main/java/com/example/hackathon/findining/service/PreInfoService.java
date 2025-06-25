package com.example.hackathon.findining.service;

import com.example.hackathon.findining.dto.PreInfoResponse;
import com.example.hackathon.findining.repository.PreInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreInfoService {

    private final PreInfoRepository preInfoRepository;

    public List<PreInfoResponse> getPreInfoByLocation(Integer location) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        return preInfoRepository
                .findAllByLocationAndCreateAtBetweenOrderByTimeAsc(location, startOfDay, endOfDay)
                .stream()
                .map(PreInfoResponse::new)
                .collect(Collectors.toList());
    }
}
