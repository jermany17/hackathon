package com.example.hackathon.config;

import com.example.hackathon.auth.service.UserDetailService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailService userService;
    private final CorsConfig corsConfig;

    // 특정 HTTP 요청에 대한 보안 규칙
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource())) // CORS 적용
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth

                        // 로그인 없이 접근 가능
                        .requestMatchers("/check-login", "/check-userid", "/signup", "/login", "/logout" ).permitAll()

                        // 로그인된 사용자만 접근 가능
                        .requestMatchers(
                                "/userinfo", "/check-password", "/update-password", "/delete-account",
                                "/s3/upload", "/s3/delete",
                                "/preinfo/*",
                                "/menu"
                        ).authenticated()
                        .anyRequest().denyAll()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> { // 인증되지 않은 사용자가 보호된 API에 접근했을 때
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"message\": \"로그인이 필요합니다.\"}");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")  // 로그아웃 API 경로
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제

                        .logoutSuccessHandler((request, response, authentication) -> {

                            response.setContentType("application/json;charset=UTF-8"); // JSON 응답 설정

                            // 로그아웃 성공 응답
                            response.setStatus(HttpServletResponse.SC_OK); // 200 OK 응답 반환
                            String jsonResponse = """
                                {
                                    "message": "로그아웃 성공."
                                }
                            """;

                            response.getWriter().write(jsonResponse);
                            response.getWriter().flush();
                        })
                )
                .build();
    }

    // 인증 관리자 관련 설정
    @Bean
    public AuthenticationManager authenticationManager(BCryptPasswordEncoder bCryptPasswordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(authProvider);
    }

    // 비밀번호 인코더로 사용, 비밀번호 암호화 후 비교
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
