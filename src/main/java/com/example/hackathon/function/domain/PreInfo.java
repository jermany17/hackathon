package com.example.hackathon.function.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pre_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "location", nullable = false)
    private Integer location;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "time", nullable = false)
    private Integer time;

    @Column(name = "weekday", nullable = false)
    private Integer weekday;

    @Column(name = "estimated_wait_time", nullable = false)
    private Integer estimatedWaitTime;

    @Builder
    public PreInfo(Integer location, Integer time, Integer weekday, Integer estimatedWaitTime) {
        this.location = location;
        this.time = time;
        this.weekday = weekday;
        this.estimatedWaitTime = estimatedWaitTime;
    }
}
