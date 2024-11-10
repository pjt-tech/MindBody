package com.study.security.member.controller;

import com.study.security.global.util.JwtUtil;
import com.study.security.member.dto.AuthResponse;
import com.study.security.member.dto.LoginRequest;
import com.study.security.member.dto.RefreshTokenRequest;
import com.study.security.member.dto.SignupRequest;
import com.study.security.member.entity.RefreshToken;
import com.study.security.member.repository.RefreshTokenRepository;
import com.study.security.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    private final AuthService authService;



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        String accessToken = jwtUtil.generateAccessToken(loginRequest.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(loginRequest.getUsername());

        // Refresh Token DB 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .username(loginRequest.getUsername())
                .token(refreshToken)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        // DB에서 refreshToken 조회
        RefreshToken refreshToken = refreshTokenRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (!refreshToken.getToken().equals(request.getRefreshToken())) {
            return ResponseEntity.status(403).body("Invalid refresh token");
        }

        String newAccessToken = jwtUtil.generateAccessToken(request.getUsername());
        return ResponseEntity.ok(new AuthResponse(newAccessToken, request.getRefreshToken()));
    }


    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
        try {
            authService.signup(signupRequest);
            return ResponseEntity.ok("회원가입 성공");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("회원가입 실패: " + e.getMessage());
        }
    }
}

