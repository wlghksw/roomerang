package com.roomerang.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰을 생성하고 유효성을 검증하는 컴포넌트 클래스.
 * 최신 JJWT 라이브러리(0.11.x)를 기준으로 수정함.
 */

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final UserDetailsService userDetailsService;

    // application.properties 또는 환경변수에서 주입받음.
    private SecretKey secretKey;

    /**
     * secretKey를 Base64 인코딩 처리.
     */
    @PostConstruct
    protected void init() {
        LOGGER.info("[init] JwtTokenProvider 내 secretKey 초기화 시작");

        // HMAC-SHA256 알고리즘을 사용하도록 보장하는 256비트 키 생성
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        LOGGER.info("[init] JwtTokenProvider 내 secretKey 초기화 완료");
    }

    /**
     * JWT 토큰 생성
     *
     * @param username 사용자 식별자 (subject)
     * @param roles   사용자 역할 목록
     * @return 생성된 JWT 토큰 문자열
     */
    public String createToken(String username, List<String> roles) {
        LOGGER.info("[createToken] 토큰 생성 시작");
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);

        Date now = new Date();
        // 토큰 유효 시간: 1시간
        long tokenValidMillisecond = 1000L * 60 * 60;

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidMillisecond))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        LOGGER.info("[createToken] 토큰 생성 완료");
        return token;
    }

    /**
     * JWT 토큰으로부터 인증 정보를 조회
     *
     * @param token JWT 토큰
     * @return Authentication 객체 (인증 정보)
     */
    public Authentication getAuthentication(String token) {
        LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 시작");
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 완료, UserDetails UserName : {}",
                userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * JWT 토큰에서 회원 구별 정보(Subject) 추출
     *
     * @param token JWT 토큰
     * @return 토큰에 담긴 subject 값
     */

    public String getUsername(String token) {
        LOGGER.info("[getUsername] 토큰 기반 회원 구별 정보 추출");
        String info = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        LOGGER.info("[getUsername] 토큰 기반 회원 구별 정보 추출 완료, info : {}", info);
        return info;
    }

    /**
     * HTTP Request Header에서 JWT 토큰 값을 추출
     *
     * @param request HttpServletRequest
     * @return 헤더에 포함된 토큰 값
     */
    public String resolveToken(HttpServletRequest request) {
        LOGGER.info("[resolveToken] HTTP 헤더에서 Token 값 추출");
        return request.getHeader("X-AUTH-TOKEN");
    }

    /**
     * JWT 토큰의 유효성 및 만료일 체크
     *
     * @param token JWT 토큰
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        LOGGER.info("[validateToken] 토큰 유효 체크 시작");
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            LOGGER.info("[validateToken] 토큰 유효 체크 완료");
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            LOGGER.info("[validateToken] 토큰 유효 체크 예외 발생: {}", e.getMessage());
            return false;
        }
    }
}
