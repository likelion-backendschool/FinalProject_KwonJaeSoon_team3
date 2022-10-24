package com.ll.ebook.app.security.service;

import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.member.repository.MemberRepository;
import com.ll.ebook.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).get();

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (member.getNickname() != null) {
            authorities.add(new SimpleGrantedAuthority("writer"));
        } else {
            authorities.add(new SimpleGrantedAuthority("member"));
        }

        return new MemberContext(member, authorities);
    }
}