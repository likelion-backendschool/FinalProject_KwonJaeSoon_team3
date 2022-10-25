package com.ll.ebook.app.base.initData;

import com.ll.ebook.app.cart.entity.CartItem;
import com.ll.ebook.app.cart.service.CartItemService;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.member.service.MemberService;
import com.ll.ebook.app.order.entity.Order;
import com.ll.ebook.app.order.service.OrderService;
import com.ll.ebook.app.post.entity.Post;
import com.ll.ebook.app.post.service.PostService;
import com.ll.ebook.app.product.entity.Product;
import com.ll.ebook.app.product.service.ProductService;

public interface InitDataBefore {
    default void before(MemberService memberService, PostService postService, ProductService productService, CartItemService cartItemService, OrderService orderService) {
        Member member1 = memberService.join("user1", "1234", "jaesoon","user1@test.com");
        Member member2 = memberService.join("user2", "1234", "jaesoon2", "user2@test.com");

        Post post1 = postService.write(member1, "제목1", "내용1", "contentHTML1", "#해시태그1 #해시태그2");
        Post post3 = postService.write(member1, "제목3", "내용3", "contentHTML3", "#해시태그1 #해시태그2");
        Post post4 = postService.write(member1, "제목4", "내용4", "contentHTML4", "#해시태그1 #해시태그2");
        Post post5 = postService.write(member1, "제목5", "내용5", "contentHTML5", "#해시태그1 #해시태그2");
        Post post2 = postService.write(member1, "제목2", "내용2", "contentHTML2", "#해시태그3 #해시태그4");

        Product product1 = productService.create(member1, "품목1", 12000, 1L, "#해시태그1 #해시태그2");
        Product product2 = productService.create(member1, "품목2", 13000, 2L, "#해시태그1 #해시태그2");
        Product product3 = productService.create(member1, "품목3", 14000, 3L, "#해시태그3 #해시태그4");
        Product product4 = productService.create(member1, "품목4", 15000, 4L, "#해시태그3 #해시태그4");

        CartItem cartItem1 = cartItemService.addItem(member1, product1);
        CartItem cartItem2 = cartItemService.addItem(member1, product2);
        CartItem cartItem3 = cartItemService.addItem(member1, product3);
        CartItem cartItem4 = cartItemService.addItem(member1, product4);

        memberService.addCash(member1, 100_000_000, "충전__무통장입금");

//
//        Order order1 = orderService.createFromCart(member1);
//
//        orderService.order(order1);
    }
}