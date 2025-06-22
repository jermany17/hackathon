package com.example.hackathon.auth.controller;

import com.example.hackathon.auth.domain.User;
import com.example.hackathon.auth.dto.*;
import com.example.hackathon.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // userId 중복 확인 API
    @GetMapping("/check-userid")
    public ResponseEntity<Map<String, String>> checkUserId(@RequestParam String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "아이디를 입력해주세요.")); // 400
        }

        boolean exists = userService.isUserIdExists(userId);

        if (exists) {
            return ResponseEntity.badRequest().body(Map.of("message", "이미 사용 중인 아이디입니다.")); // 400
        } else {
            return ResponseEntity.ok(Map.of("message", "사용 가능한 아이디입니다."));
        }
    }

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody AddUser request) {
        try {
            Long userId = userService.save(request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "회원가입이 완료되었습니다.");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "회원가입 실패"));
        }
    }

    // 로그인 API (세션 방식)
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserLogin request, HttpServletRequest httpServletRequest) {
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(request.getUserId(), request.getUserPassword());


            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            // 세션을 생성하고 SecurityContext를 저장
            HttpSession session = httpServletRequest.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "로그인 성공");
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            String errorMessage = "로그인 실패";

            if (e.getMessage().contains("존재하지 않는 사용자")) {
                errorMessage = "존재하지 않는 아이디입니다.";
            } else if (e.getMessage().contains("자격 증명에 실패하였습니다") || e.getMessage().contains("Bad credentials")) {
                errorMessage = "비밀번호가 틀렸습니다.";
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", errorMessage));
        }
    }

    // 로그아웃 API = WebSecurityConfig에서 처리

    // 로그인 여부 확인 API
    @GetMapping("/check-login")
    public ResponseEntity<Map<String, Object>> checkLoginStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 사용자가 인증되었는지 확인
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("isLoggedIn", false, "message", "로그인되지 않은 사용자입니다."));
        }

        return ResponseEntity.ok(Map.of("isLoggedIn", true, "message", "로그인된 사용자입니다."));
    }

    // 로그인된 사용자 정보 조회 API (비밀번호 제외)
    @GetMapping("/userinfo")
    public ResponseEntity<UserInfo> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 로그인된 사용자 정보 반환 (비밀번호 제외)
        User user = (User) authentication.getPrincipal();
        UserInfo userInfo = new UserInfo(user);

        return ResponseEntity.ok(userInfo);
    }

    // 현재 비밀번호 확인 API
    @PostMapping("/check-password")
    public ResponseEntity<Map<String, String>> checkPassword(@RequestBody CheckUserPassword checkUserPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();  // 현재 로그인한 사용자 가져오기

        // 현재 비밀번호가 일치하는지 확인
        if (!bCryptPasswordEncoder.matches(checkUserPassword.getCurrentPassword(), user.getUserPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "현재 비밀번호가 일치하지 않습니다."));
        }

        return ResponseEntity.ok(Map.of("message", "현재 비밀번호가 일치합니다."));
    }

    // 비밀번호 변경 API
    @PutMapping("/update-password")
    public ResponseEntity<Map<String, String>> updatePassword(@RequestBody UpdateUserPassword updateUserPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();  // 현재 로그인한 사용자 가져오기

        // 현재 비밀번호가 일치하는지 확인
        if (!bCryptPasswordEncoder.matches(updateUserPassword.getCurrentPassword(), user.getUserPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "현재 비밀번호가 일치하지 않습니다."));
        }

        // 새로운 비밀번호 암호화 후 서비스 호출
        String encodedNewPassword = bCryptPasswordEncoder.encode(updateUserPassword.getNewPassword());
        userService.updatePassword(user, encodedNewPassword);

        return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
    }

    // 회원 삭제 API
    @DeleteMapping("/delete-account")
    public ResponseEntity<Map<String, String>> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal(); // 현재 로그인한 사용자 가져오기

        userService.deleteUser(user.getUserId()); // userId만 Service로 전달

        return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 완료되었습니다."));
    }

}

