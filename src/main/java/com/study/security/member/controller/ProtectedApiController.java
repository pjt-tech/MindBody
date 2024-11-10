package com.study.security.member.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class ProtectedApiController {

    // 이 API는 인증된 사용자만 접근할 수 있도록 설정됩니다.
    @GetMapping("/protected")
    @PreAuthorize("isAuthenticated()")  // 인증된 사용자만 접근 가능
    public String getProtectedData() {
        return "This is protected data, accessible only with a valid JWT token.";
    }
}
