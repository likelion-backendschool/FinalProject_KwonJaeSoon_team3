package com.ll.ebook.app.base.initData;

import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.member.service.MemberService;

public interface InitDataBefore {
    default void before(MemberService memberService) {
        Member member1 = memberService.join("user1", "1234", "jaesoon","user1@test.com");
        Member member2 = memberService.join("user2", "1234", "jaesoon2", "user2@test.com");
    }
}