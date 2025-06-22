package com.example.hackathon.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLogin {
    private String userId; // 로그인 ID
    private String userPassword; // 비밀번호
}
