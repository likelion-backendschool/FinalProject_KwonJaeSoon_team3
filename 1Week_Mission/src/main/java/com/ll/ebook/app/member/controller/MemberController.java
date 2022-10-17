package com.ll.ebook.app.member.controller;

import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.member.form.JoinForm;
import com.ll.ebook.app.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/login")
    public String showLogin(HttpServletRequest request) {
        String uri = request.getHeader("Referer");
        if (uri != null && !uri.contains("/member/login")) {
            request.getSession().setAttribute("prevPage", uri);
        }

        return "member/login";
    }

    @GetMapping("/join")
    public String showJoin() {
        return "member/join";
    }

    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm) {
        Member oldMember = memberService.findMemberByUsername(joinForm.getUsername());

        if(oldMember != null) {
            return "redirect:/?errorMsg=Already Join";
        }

        memberService.join(joinForm.getUsername(), joinForm.getPassword(), joinForm.getNickname(), joinForm.getEmail());

        return "redirect:/";
    }
}