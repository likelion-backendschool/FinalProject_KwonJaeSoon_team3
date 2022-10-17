package com.ll.ebook.app.member.service;

import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member join(String username, String password, String nickname, String email) {
        if (memberRepository.findByUsername(username).isPresent()) {

        }
        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .email(email)
                .build();

        memberRepository.save(member);

        return member;
    }

    public Member findMemberByUsername(String username) {
        return memberRepository.findByUsername(username).orElse(null);
    }

    public void modify(Member member, String nickname, String email) {
        member.setNickname(nickname);
        member.setEmail(email);

        memberRepository.save(member);
    }
}
