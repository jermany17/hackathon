package com.example.hackathon.findining.dto;

import com.example.hackathon.findining.domain.PreInfo;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PreInfoResponse {
    private final Long id;
    private final Integer location;
    private final LocalDateTime createAt;
    private final Integer time;
    private final Integer weekday;
    private final Integer estimatedWaitTime;

    public PreInfoResponse(PreInfo preInfo) {
        this.id = preInfo.getId();
        this.location = preInfo.getLocation();
        this.createAt = preInfo.getCreateAt();
        this.time = preInfo.getTime();
        this.weekday = preInfo.getWeekday();
        this.estimatedWaitTime = preInfo.getEstimatedWaitTime() / 60;
    }
}
