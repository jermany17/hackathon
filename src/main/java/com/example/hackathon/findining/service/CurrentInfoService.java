package com.example.hackathon.findining.service;

import com.example.hackathon.findining.dto.CurrentInfoRequest;
import com.example.hackathon.findining.dto.CurrentInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrentInfoService {

    private static final String EXTERNAL_URL = "https://bbb5-223-195-38-166.ngrok-free.app/function1";

    public CurrentInfoResponse getEstimatedWaitTime(int location, int weekday) {
        RestTemplate restTemplate = new RestTemplate();

        // 👉 클라이언트로부터 받은 location, weekday 반영
        CurrentInfoRequest requestBody = new CurrentInfoRequest();
        requestBody.setLocation(location);
        requestBody.setWeekday(weekday);
        requestBody.setCurrent_queue_length(0);       // TODO: 추후 외부 API 등에서 설정
        requestBody.setCurrent_seated_count(0);       // TODO: 추후 외부 API 등에서 설정
        requestBody.setCurrent_order_backlog(30);

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
}
