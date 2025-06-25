package com.example.hackathon.function.service;

import com.example.hackathon.function.dto.CurrentInfoRequest;
import com.example.hackathon.function.dto.CurrentInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrentInfoService {

    private static final String EXTERNAL_URL = "https://bbb5-223-195-38-166.ngrok-free.app/function1";

    public CurrentInfoResponse getEstimatedWaitTime() {
        RestTemplate restTemplate = new RestTemplate();

        // 👉 서비스에서 동적으로 요청값 초기화
        CurrentInfoRequest requestBody = new CurrentInfoRequest();
        requestBody.setCurrent_queue_length(0);      // TODO: 추후 다른 API에서 값 불러와 설정
        requestBody.setCurrent_seated_count(0);      // TODO: 추후 다른 API에서 값 불러와 설정
        requestBody.setCurrent_order_backlog(0);     // TODO: 추후 DB에서 불러와 설정

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
