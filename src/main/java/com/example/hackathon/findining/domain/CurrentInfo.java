package com.example.hackathon.findining.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "current_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CurrentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private int location;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(nullable = false)
    private int time;

    @Column(nullable = false)
    private int weekday;

    @Column(name = "current_queue_length", nullable = false)
    private int currentQueueLength;

    @Column(name = "current_seated_count", nullable = false)
    private int currentSeatedCount;

    @Column(name = "current_order_backlog", nullable = false)
    private int currentOrderBacklog;

    @Column(name = "estimated_wait_time", nullable = false)
    private int estimatedWaitTime;
}
