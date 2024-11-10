package com.study.security.global.filter;

import com.study.security.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청에서 Authorization 헤더 가져오기
        String token = getTokenFromRequest(request);

        // 2. 토큰이 존재하고 유효한지 확인
        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            // 3. 토큰이 유효하면 사용자 인증 정보 설정
            String username = jwtUtil.getUsernameFromToken(token);
            var authentication = new UsernamePasswordAuthenticationToken(username, null, null); // 인증된 사용자 객체 생성
            SecurityContextHolder.getContext().setAuthentication(authentication); // 인증 정보 설정
        }

        // 4. 요청을 계속 필터 체인에 넘김
        filterChain.doFilter(request, response);
    }

    // HTTP 요청에서 Authorization 헤더에서 토큰 추출
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 부분을 제거한 토큰 반환
        }
        return null;
    }
}

