package com.example.hackathon.findining.repository;

import com.example.hackathon.findining.domain.PreInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PreInfoRepository extends JpaRepository<PreInfo, Long> {
    List<PreInfo> findAllByLocationAndCreateAtBetweenOrderByTimeAsc(
            Integer location, LocalDateTime start, LocalDateTime end
    );
}
