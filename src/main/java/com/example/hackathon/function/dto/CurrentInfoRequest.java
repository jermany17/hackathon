package com.example.hackathon.function.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentInfoRequest {
    private int current_queue_length;
    private int current_seated_count;
    private int current_order_backlog;
}
