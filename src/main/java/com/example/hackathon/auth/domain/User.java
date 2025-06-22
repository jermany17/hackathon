package com.example.hackathon.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "user_name", nullable = false)
    private String userName; // 사용자 이름

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId; // 로그인 ID

    @Column(name = "user_password", nullable = false)
    private String userPassword; // 비밀번호

    @Column(name = "user_birthday", nullable = false)
    private LocalDate userBirthday;

    @Column(name = "user_gender", nullable = false)
    private String userGender;

    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt; // 생성일시 (최초 생성 이후 변경되지 않음)

    @Column(name = "update_at")
    private LocalDateTime updateAt; // 수정일시


    @Builder
    public User(String userName, String userId, String userPassword, LocalDate userBirthday, String userGender) {
        this.userName = userName;
        this.userId = userId;
        this.userPassword = userPassword;
        this.userBirthday = userBirthday;
        this.userGender = userGender;
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateAt = LocalDateTime.now(); // 엔터티가 수정될 때 자동으로 updateAt 갱신
    }

    // 비밀번호 변경 메서드 추가
    public void changePassword(String newPassword) {
        this.userPassword = newPassword;
        this.updateAt = LocalDateTime.now(); // 비밀번호 변경 시 updateAt 자동 업데이트
    }

    public String getUserName() { // 이건 User에서 정의한 메서드
        return this.userName; // getUsername()과의 충돌 방지
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // 사용자의 권한 목록
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getUsername() { // 이건 UserDetails 인터페이스에 정의되어있는 메서드
        return userId; // 아이디로 userId 사용한다는 의미
    }

    @Override
    public String getPassword() {
        return userPassword;
    }

    @Override
    public boolean isAccountNonExpired() { // 계정 만료 여부
        return true;
    }

    @Override
    public boolean isAccountNonLocked() { // 계정 잠금 여부
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { // 비밀번호 만료 여부
        return true;
    }

    @Override
    public boolean isEnabled() { // 계정 사용 가능 여부
        return true;
    }
}
