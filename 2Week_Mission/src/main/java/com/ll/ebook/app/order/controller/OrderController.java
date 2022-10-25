package com.ll.ebook.app.order.controller;

import com.ll.ebook.app.cart.entity.CartItem;
import com.ll.ebook.app.cart.exception.EmptyProductException;
import com.ll.ebook.app.cart.service.CartItemService;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.order.entity.Order;
import com.ll.ebook.app.order.service.OrderService;
import com.ll.ebook.app.product.entity.Product;
import com.ll.ebook.app.security.dto.MemberContext;
import com.ll.ebook.util.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String addItem(@AuthenticationPrincipal MemberContext memberContext) {
        Member member = memberContext.getMember();

        orderService.createFromCart(member);

        return Rq.redirectWithMsg("/cart/list", "주문이 완료되었습니다!");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String showList(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        List<Order> orderList = orderService.findAllByMemberId(memberContext.getMember().getId());

        model.addAttribute("orderList", orderList);

        return "order/list";
    }
}
