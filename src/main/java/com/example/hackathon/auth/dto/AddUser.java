package com.example.hackathon.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AddUser {
    private String userName; // 사용자 이름
    private String userId; // 로그인 ID
    private String userPassword; // 비밀번호
    private LocalDate userBirthday;   // 생년월일
    private String userGender;        // 성별
}
