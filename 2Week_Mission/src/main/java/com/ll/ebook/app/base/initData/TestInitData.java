package com.ll.ebook.app.base.initData;

import com.ll.ebook.app.cart.service.CartItemService;
import com.ll.ebook.app.member.service.MemberService;
import com.ll.ebook.app.order.service.OrderService;
import com.ll.ebook.app.post.service.PostService;
import com.ll.ebook.app.product.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestInitData implements InitDataBefore {
    @Bean
    CommandLineRunner initData(MemberService memberService, PostService postService, ProductService productService, CartItemService cartItemService, OrderService orderService) {
        return args -> {
            before(memberService, postService, productService, cartItemService, orderService);
        };
    }
}