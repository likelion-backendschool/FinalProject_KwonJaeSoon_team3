package com.ll.ebook.app.member.controller;

import com.ll.ebook.app.contact.service.ContactService;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.member.form.JoinForm;
import com.ll.ebook.app.member.service.MemberService;
import com.ll.ebook.app.security.dto.MemberContext;
import com.ll.ebook.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final ContactService contactService;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin() {
        return "member/join";
    }

    @PostMapping("/join")
    public String join(HttpServletRequest req, @Valid JoinForm joinForm) {
        Member oldMember = memberService.findMemberByUsername(joinForm.getUsername());

        if(oldMember != null) {
            String msg = Util.url.encode("이미 존재하는 회원입니다..");
            return "redirect:/member/join?msg=%s".formatted(msg);
        }

        String nickname = joinForm.getNickname();

        if(nickname.equals("")) {
            nickname = null;
        }

        Member member = memberService.join(joinForm.getUsername(), joinForm.getPassword(), nickname, joinForm.getEmail());

        try {
            req.login(joinForm.getUsername(), joinForm.getPassword());
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        String title = "%s님의 회원가입 축하메시지".formatted(joinForm.getUsername());
        String joinMsg= "$s님의 회원가입을 축하합니다!!!!!".formatted(joinForm.getUsername());

        contactService.sendSimpleMessage(member, title, joinMsg);

        return "redirect:/member/profile";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin() {
        return "member/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model) {
        Member loginedMember = memberService.findMemberByUsername(principal.getName());

        model.addAttribute("loginedMember", loginedMember);

        return "member/profile";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public String showModify(@AuthenticationPrincipal MemberContext context, Model model) {
        Member loginedMember = memberService.findMemberByUsername(context.getUsername());

        model.addAttribute("loginedMember", loginedMember);

        return "member/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String modify(@AuthenticationPrincipal MemberContext context, String nickname, String email) {
        Member member = memberService.findMemberByUsername(context.getUsername());

        memberService.modify(member, nickname, email);

        return "redirect:/member/profile";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modifyPassword")
    public String showModifyPassword() {

        return "member/modifyPassword";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modifyPassword")
    public String modifyPassword(@AuthenticationPrincipal MemberContext context, String password, String modifyPassword) {
        Member loginedMember = memberService.findMemberByUsername(context.getUsername());

        if(!passwordEncoder.matches(password, loginedMember.getPassword())) {
            String msg = Util.url.encode("현재 비밀번호가 틀립니다.");
            return "redirect:/member/modifyPassword?msg=%s".formatted(msg);
        }

        memberService.modifyPassword(loginedMember, modifyPassword);

        return "redirect:/member/profile";
    }



    @GetMapping("/findUsername")
    public String showFindUsername() {
        return "member/findUsername";
    }

    @PostMapping("/findUsername")
    public String findUsername(Model model, String email) {
        Member member = memberService.findMemberByEmail(email);

        model.addAttribute("member", member);

        return "member/findUsername";
    }

    @GetMapping("/findPassword")
    public String showFindPassword() {
        return "member/findPassword";
    }

    @PostMapping("/findPassword")
    public String findPassword(String username, String email) {
        Member member = memberService.findMemberByUsernameAndEmail(username, email);

        if(member == null) {
            String msg = Util.url.encode("가입된 아이디가 없습니다.");
            return "redirect:/member/findPassword?msg=%s".formatted(msg);
        }

        String newPassword = "";
        for(int i = 0; i < 10; i++) {
            char ran = (char)((int)(Math.random() * 25) + 97);
            newPassword += ran;
        }

        memberService.setNewPassword(member, newPassword);

        contactService.sendSimpleMessage(member, newPassword);

        return "redirect:/member/login";
    }
}