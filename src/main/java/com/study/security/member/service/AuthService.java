package com.study.security.member.service;

import com.study.security.member.dto.SignupRequest;
import com.study.security.member.entity.Member;
import com.study.security.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 회원가입 메서드
    @Transactional
    public void signup(SignupRequest signupRequest) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        // Member 엔티티 생성
        Member member = new Member(
                signupRequest.getUsername(),
                encodedPassword,
                signupRequest.getEmail()
        );

        // DB에 저장
        memberRepository.save(member);
    }
}
