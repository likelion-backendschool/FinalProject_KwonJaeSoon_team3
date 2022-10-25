package com.ll.ebook.app.member.service;

import com.ll.ebook.app.cash.entity.CashLog;
import com.ll.ebook.app.cash.service.CashLogService;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.member.repository.MemberRepository;
import com.ll.ebook.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CashLogService cashLogService;


    public Member join(String username, String password, String nickname, String email) {
        int authLevel = 3;

        if( nickname != null) {
            authLevel = 7;
        }

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .email(email)
                .authLevel(authLevel)
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

        int authLevel = 3;

        if( nickname != null) {
            authLevel = 7;
        }

        member.setAuthLevel(authLevel);

        memberRepository.save(member);
    }

    public Optional<Member> findMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Member findMemberByUsernameAndEmail(String username, String email) {
        return memberRepository.findByUsernameAndEmail(username, email);
    }

    public void setNewPassword(Member member, String newPassword) {
        String encodePassword = passwordEncoder.encode(newPassword);
        member.setPassword(encodePassword);

        memberRepository.save(member);
    }

    public void modifyPassword(Member member, String newPassword) {
        String encodePassword = passwordEncoder.encode(newPassword);
        member.setPassword(encodePassword);

        memberRepository.save(member);
    }

    @Transactional
    public long addCash(Member member, long price, String eventType) {
        CashLog cashLog = cashLogService.addCash(member, price, eventType);

        long newRestCash = member.getRestCash() + cashLog.getPrice();
        member.setRestCash(newRestCash);
        memberRepository.save(member);

        return newRestCash;
    }

    public long getRestCash(Member member) {
        return member.getRestCash();
    }
}