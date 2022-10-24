package com.ll.ebook.app.base.initData;

import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.member.service.MemberService;
import com.ll.ebook.app.post.entity.Post;
import com.ll.ebook.app.post.service.PostService;

public interface InitDataBefore {
    default void before(MemberService memberService, PostService postService) {
        Member member1 = memberService.join("user1", "1234", "jaesoon","user1@test.com");
        Member member2 = memberService.join("user2", "1234", "jaesoon2", "user2@test.com");

        Post post1 = postService.write(member1, "제목1", "내용1", "contentHTML1", "#해시태그1 #해시태그2");
        Post post3 = postService.write(member1, "제목3", "내용3", "contentHTML3", "#해시태그1 #해시태그2");
        Post post4 = postService.write(member1, "제목4", "내용4", "contentHTML4", "#해시태그1 #해시태그2");
        Post post5 = postService.write(member1, "제목5", "내용5", "contentHTML5", "#해시태그1 #해시태그2");
        Post post2 = postService.write(member1, "제목2", "내용2", "contentHTML2", "#해시태그3 #해시태그4");
    }
}