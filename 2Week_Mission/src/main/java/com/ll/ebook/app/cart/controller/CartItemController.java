package com.ll.ebook.app.cart.controller;

import com.ll.ebook.app.cart.entity.CartItem;
import com.ll.ebook.app.cart.service.CartItemService;
import com.ll.ebook.app.security.dto.MemberContext;
import com.ll.ebook.util.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartItemController {
    private final CartItemService cartItemService;
    private final Rq rq;

    @GetMapping("/list")
    public String showList( Model model) {
        List<CartItem> cartItems = cartItemService.findAll();

        model.addAttribute("cartItems", cartItems);

        return "cart/list";
    }
}
