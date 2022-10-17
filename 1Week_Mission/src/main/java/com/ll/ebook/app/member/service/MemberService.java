package com.ll.ebook.app.member.service;

import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
