package com.ll.ebook.app.member.controller;

import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.member.form.JoinForm;
import com.ll.ebook.app.member.service.MemberService;
import com.ll.ebook.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
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
            return "redirect:/?errorMsg=Already Join";
        }

        memberService.join(joinForm.getUsername(), joinForm.getPassword(), joinForm.getNickname(), joinForm.getEmail());

        try {
            req.login(joinForm.getUsername(), joinForm.getPassword());
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

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
}
