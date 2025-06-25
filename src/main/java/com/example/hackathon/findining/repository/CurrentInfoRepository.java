package com.example.hackathon.findining.repository;

import com.example.hackathon.findining.domain.CurrentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CurrentInfoRepository extends JpaRepository<CurrentInfo, Integer> {
    List<CurrentInfo> findAllByCreateAtBetweenAndTimeAndLocation(
            LocalDateTime start, LocalDateTime end, Integer time, Integer location
    );
}
