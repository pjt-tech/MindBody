package com.study.security.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final Key key;
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 30;  // 15분
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7;  // 7일

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        // JWT에서 사용될 비밀 키 생성
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);  // HS256 알고리즘을 사용하여 키 생성
    }


    public String generateAccessToken(String username) {
        return createToken(username, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(String username) {
        return createToken(username, REFRESH_TOKEN_EXPIRATION);
    }

    private String createToken(String subject, long expiration) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 토큰이 유효하지 않거나 만료된 경우 예외 처리
            return false;
        }
    }

    // 토큰에서 사용자 이름 추출
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        return claims.getSubject(); // 사용자 이름 반환
    }
}
