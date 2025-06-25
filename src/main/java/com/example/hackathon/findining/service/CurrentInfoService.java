package com.example.hackathon.findining.service;

import com.example.hackathon.findining.domain.CurrentInfo;
import com.example.hackathon.findining.dto.CurrentInfoRequest;
import com.example.hackathon.findining.dto.CurrentInfoResponse;
import com.example.hackathon.findining.dto.RecommendTimeResponse;
import com.example.hackathon.findining.repository.CurrentInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrentInfoService {

    private static final String EXTERNAL_URL = "https://bbb5-223-195-38-166.ngrok-free.app/real_wait_time";
    private static final String QUEUE_COUNT_URL = "https://bbb5-223-195-38-166.ngrok-free.app/current_waiting_cnt";
    private static final String SEATED_COUNT_URL = "https://bbb5-223-195-38-166.ngrok-free.app/current_dining_cnt";

    private final RestTemplate restTemplate = new RestTemplate();
    private final CurrentInfoRepository currentInfoRepository;

    public CurrentInfoResponse getEstimatedWaitTime(int location, int weekday) {
        int queueCount = fetchPersonCount(QUEUE_COUNT_URL);
        int seatedCount = fetchPersonCount(SEATED_COUNT_URL);

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

        int divided = Math.round(estimatedWaitTime / 60.0f);
        return new CurrentInfoResponse(divided);
    }

    // 1시간마다 자동 실행 (매 정시)
    @Scheduled(cron = "0 0 * * * *") // 매 정시마다
    public void storeHourlyCurrentInfos() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour(); // time 컬럼용
        int weekday = now.getDayOfWeek().getValue() - 1; // 월=1 ~ 일=7 → 월=0 ~ 일=6

        for (int location = 0; location <= 4; location++) {
            try {
                int queue = fetchPersonCount(QUEUE_COUNT_URL);
                int seated = fetchPersonCount(SEATED_COUNT_URL);
                int backlog = 30;

                int estimated = fetchEstimatedWaitTime(location, weekday, queue, seated, backlog);

                System.out.println("저장 준비 - location: " + location);

                CurrentInfo currentInfo = new CurrentInfo(
                        null,                 // ID (auto-generated)
                        location,
                        now,
                        hour,
                        weekday,
                        queue,
                        seated,
                        backlog,
                        estimated
                );

                currentInfoRepository.save(currentInfo);
                System.out.println("저장 성공 - location: " + location);
            } catch(Exception e) {
                System.out.println("저장 실패 - location: " + location + ", 이유: " + e.getMessage());
            }
        }
    }

    // 외부 API 호출하여 예상 대기 시간 추출
    private int fetchEstimatedWaitTime(int location, int weekday, int queue, int seated, int backlog) {
        CurrentInfoRequest request = new CurrentInfoRequest();
        request.setLocation(location);
        request.setWeekday(weekday);
        request.setCurrent_queue_length(queue);
        request.setCurrent_seated_count(seated);
        request.setCurrent_order_backlog(backlog);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CurrentInfoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                EXTERNAL_URL, HttpMethod.POST, entity, Map.class);

        Map<String, Object> body = response.getBody();
        int estimatedWaitTime = 0;

        if (body != null && body.containsKey("estimated_wait_time")) {
            Object value = body.get("estimated_wait_time");
            if (value instanceof Number) {
                estimatedWaitTime = ((Number) value).intValue();
            }
        }

        return Math.round(estimatedWaitTime / 60.0f);
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
            System.out.println("API 호출 실패 (" + url + "): " + e.getMessage());
        }
        return 0;
    }

    public RecommendTimeResponse recommendLocation(int weekday) {
        int minWaitTime = Integer.MAX_VALUE;
        int bestLocation = -1;

        for (int location = 0; location <= 4; location++) {
            int waitTime = getEstimatedWaitTime(location, weekday).getEstimated_wait_time();

            if (waitTime < minWaitTime || (waitTime == minWaitTime && location < bestLocation)) {
                minWaitTime = waitTime;
                bestLocation = location;
            }
        }

        return new RecommendTimeResponse(bestLocation);
    }
}
