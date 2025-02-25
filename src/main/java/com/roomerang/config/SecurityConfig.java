package com.roomerang.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    @ConditionalOnProperty(name = "spring.h2.console.enabled", havingValue = "true")
    public WebSecurityCustomizer configureH2ConsoleEnable() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toH2Console());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        http
                // 기본 HTTP Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // CSRF 보호 비활성화 (stateless API인 경우)
                .csrf(AbstractHttpConfigurer::disable)
                // 세션 관리 정책 변경 (IF_REQUIRED: 로그인 시에만 세션 생성)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().migrateSession() // 기존 세션 유지하며 보안 적용
                        .maximumSessions(5)
                        .maxSessionsPreventsLogin(false)
                )

                // URL 별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/board/**", "/share/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll() // 업로드된 파일 경로 허용
                        .requestMatchers( "/favorite/**").permitAll()
                        .requestMatchers("/chat/**").permitAll()
                        .requestMatchers("/users/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
