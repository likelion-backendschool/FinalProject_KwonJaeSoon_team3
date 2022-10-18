package com.ll.ebook.app.base.initData;

import com.ll.ebook.app.member.service.MemberService;
import com.ll.ebook.app.post.service.PostService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestInitData implements InitDataBefore {
    @Bean
    CommandLineRunner initData(MemberService memberService, PostService postService) {
        return args -> {
            before(memberService, postService);
        };
    }
}