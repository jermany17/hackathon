package com.example.hackathon.findining.service;

import com.example.hackathon.findining.domain.CurrentInfo;
import com.example.hackathon.findining.domain.PreInfo;
import com.example.hackathon.findining.repository.CurrentInfoRepository;
import com.example.hackathon.findining.repository.PreInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PreWaitTimeService {
    private final CurrentInfoRepository currentInfoRepository;
    private final PreInfoRepository preInfoRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private final String AI_SERVER_URL = "https://bbb5-223-195-38-166.ngrok-free.app/future_wait_time";

    public List<Map<String, Object>> getOrGenerateTodayPredictions() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        System.out.println("[INFO] Checking if today pre_info data exists: " + start + " ~ " + end);
        List<PreInfo> todayData = preInfoRepository.findAllByCreateAtBetween(start, end);
        System.out.println("[INFO] Found " + todayData.size() + " records in pre_info");

        if (todayData.isEmpty()) {
            System.out.println("[INFO] No data found for today, generating predictions...");
            generatePredictedWaitTimes();

            todayData = preInfoRepository.findAllByCreateAtBetween(start, end);
            System.out.println("[INFO] After generation, found " + todayData.size() + " records in pre_info");
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (PreInfo info : todayData) {
            Map<String, Object> map = new HashMap<>();
            map.put("create_at", info.getCreateAt());
            map.put("location", info.getLocation());
            map.put("time", info.getTime());
            map.put("weekday", info.getWeekday());
            map.put("estimated_wait_time_min", Math.round(info.getEstimatedWaitTime() / 60.0f));
            result.add(map);
        }

        System.out.println("[INFO] Returning response with size: " + result.size());
        return result;
    }

    public void generatePredictedWaitTimes() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate lastWeek = today.minusWeeks(1);

        for (int location = 0; location <= 4; location++) {
            int open = (location == 3) ? 10 : 11;
            int close = 19;

            for (int hour = open; hour < close; hour++) {
                if ((location <= 2) && hour == 15) continue;

                List<CurrentInfo> dayInfos = currentInfoRepository
                        .findAllByCreateAtBetweenAndTimeAndLocation(
                                yesterday.atStartOfDay(), yesterday.plusDays(1).atStartOfDay(), hour, location);
                List<CurrentInfo> weekInfos = currentInfoRepository
                        .findAllByCreateAtBetweenAndTimeAndLocation(
                                lastWeek.atStartOfDay(), lastWeek.plusDays(1).atStartOfDay(), hour, location);

                if (dayInfos.isEmpty() || weekInfos.isEmpty()) {
                    System.out.println("[WARN] Missing data for loc=" + location + " time=" + hour);
                    continue;
                }

                CurrentInfo dayInfo = dayInfos.get(0);
                CurrentInfo weekInfo = weekInfos.get(0);

                Map<String, Object> req = new HashMap<>();
                req.put("location", location);
                req.put("week_queue_length", weekInfo.getCurrentQueueLength());
                req.put("week_seated_count", weekInfo.getCurrentSeatedCount());
                req.put("week_order_backlog", weekInfo.getCurrentOrderBacklog());
                req.put("week_weekday", weekInfo.getWeekday());
                req.put("day_queue_length", dayInfo.getCurrentQueueLength());
                req.put("day_seated_count", dayInfo.getCurrentSeatedCount());
                req.put("day_order_backlog", dayInfo.getCurrentOrderBacklog());
                req.put("day_weekday", dayInfo.getWeekday());

                System.out.println("[INFO] Sending request to AI server: " + req);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(req, headers);

                try {
                    ResponseEntity<Map> response = restTemplate.exchange(
                            AI_SERVER_URL,
                            HttpMethod.POST,
                            request,
                            Map.class
                    );

                    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                        Double raw = (Double) response.getBody().get("average_prediction");
                        Integer estimatedWaitTime = (int) Math.round(raw);
                        System.out.println("[INFO] Received estimated time from AI: " + estimatedWaitTime);

                        PreInfo preInfo = PreInfo.builder()
                                .location(location)
                                .time(hour)
                                .weekday(today.getDayOfWeek().getValue()-1)
                                .estimatedWaitTime(estimatedWaitTime)
                                .build();

                        preInfoRepository.save(preInfo);
                        System.out.println("[INFO] Saved PreInfo: loc=" + location + ", hour=" + hour);
                    } else {
                        System.out.println("[ERROR] AI server responded with: " + response.getStatusCode());
                    }
                } catch (Exception e) {
                    System.out.println("[ERROR] Exception during AI call: " + e.getMessage());
                }
            }
        }}
    }
