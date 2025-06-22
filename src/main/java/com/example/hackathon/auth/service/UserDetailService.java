package com.example.hackathon.auth.service;

import com.example.hackathon.auth.domain.User;
import com.example.hackathon.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {

    // 스프링 시큐리티에서 사용자 정보를 가져옴
    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자: " + userId));
    }
}

