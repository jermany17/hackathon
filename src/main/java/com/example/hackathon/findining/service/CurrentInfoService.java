package com.example.hackathon.findining.service;

import com.example.hackathon.findining.dto.CurrentInfoRequest;
import com.example.hackathon.findining.dto.CurrentInfoResponse;
import com.example.hackathon.findining.dto.RecommendTimeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrentInfoService {

    private static final String EXTERNAL_URL = "https://bbb5-223-195-38-166.ngrok-free.app/real_wait_time";
    private static final String QUEUE_COUNT_URL = "https://bbb5-223-195-38-166.ngrok-free.app/current_waiting_cnt";
    private static final String SEATED_COUNT_URL = "https://bbb5-223-195-38-166.ngrok-free.app/current_dining_cnt";

    private final RestTemplate restTemplate = new RestTemplate();

    public CurrentInfoResponse getEstimatedWaitTime(int location, int weekday) {
        int queueCount = fetchPersonCount(QUEUE_COUNT_URL);
        int seatedCount = fetchPersonCount(SEATED_COUNT_URL);

        // 👉 클라이언트로부터 받은 location, weekday 반영
        CurrentInfoRequest requestBody = new CurrentInfoRequest();
        requestBody.setLocation(location);
        requestBody.setWeekday(weekday);
        requestBody.setCurrent_queue_length(queueCount);
        requestBody.setCurrent_seated_count(seatedCount);
        requestBody.setCurrent_order_backlog(30); // 고정값

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CurrentInfoRequest> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                EXTERNAL_URL,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        Map<String, Object> body = response.getBody();
        int estimatedWaitTime = 0;

        if (body != null && body.containsKey("estimated_wait_time")) {
            Object value = body.get("estimated_wait_time");
            if (value instanceof Number) {
                estimatedWaitTime = ((Number) value).intValue();
            }
        }

        int divided = estimatedWaitTime / 60;
        return new CurrentInfoResponse(divided);
    }

    public RecommendTimeResponse recommendLocation(int weekday) {
        int minWaitTime = Integer.MAX_VALUE;
        int bestLocation = -1;

        for (int location = 0; location <= 5; location++) {
            int waitTime = getEstimatedWaitTime(location, weekday).getEstimated_wait_time();

            if (waitTime < minWaitTime || (waitTime == minWaitTime && location < bestLocation)) {
                minWaitTime = waitTime;
                bestLocation = location;
            }
        }

        return new RecommendTimeResponse(bestLocation);
    }

    private int fetchPersonCount(String url) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("person_count")) {
                Object value = body.get("person_count");
                if (value instanceof Number) {
                    return ((Number) value).intValue();
                }
            }
        } catch (Exception e) {
            // 예외 발생 시 기본값 0 반환
            System.out.println("API 호출 실패 (" + url + "): " + e.getMessage());
        }
        return 0;
    }
}
