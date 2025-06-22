package com.example.hackathon.auth.dto;

import com.example.hackathon.auth.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {
    private String userName;
    private String userId;
    private String userBirthday;
    private String userGender;
    private String createAt;
    private String updateAt;

    public UserInfo(User user) {
        this.userName = user.getUserName();
        this.userId = user.getUserId();
        this.userBirthday = user.getUserBirthday().toString();
        this.userGender = user.getUserGender();
        this.createAt = user.getCreateAt().toString();
        this.updateAt = user.getUpdateAt().toString();
    }
}
