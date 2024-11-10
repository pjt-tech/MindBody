package com.study.security.member.service;

import com.study.security.member.entity.Member;
import com.study.security.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 사용자 이름에 해당하는 사용자 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // UserDetails 객체 생성 (비밀번호는 암호화된 비밀번호를 사용)
        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword()) // 암호화된 비밀번호
                .roles("USER")  // 기본적으로 "USER" 역할을 부여
                .build();
    }
}

