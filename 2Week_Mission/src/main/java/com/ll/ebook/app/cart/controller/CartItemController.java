package com.ll.ebook.app.cart.controller;

import com.ll.ebook.app.cart.entity.CartItem;
import com.ll.ebook.app.cart.exception.EmptyProductException;
import com.ll.ebook.app.cart.service.CartItemService;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.product.entity.Product;
import com.ll.ebook.app.product.exception.ActorCanNotRemoveException;
import com.ll.ebook.app.product.service.ProductService;
import com.ll.ebook.app.security.dto.MemberContext;
import com.ll.ebook.util.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
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
@RequestMapping("/cart")
public class CartItemController {
    private final CartItemService cartItemService;
    private final ProductService productService;
    private final Rq rq;

    @GetMapping("/list")
    public String showList( Model model) {
        List<CartItem> cartItems = cartItemService.findAll();

        model.addAttribute("cartItems", cartItems);

        return "cart/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/add/{productId}")
    public String addItem(@AuthenticationPrincipal MemberContext memberContext, @PathVariable Long productId) {
        Optional<Product> product = productService.findById(productId);

        if(product.isEmpty()) {
            throw new EmptyProductException();
        }

        cartItemService.addItem(memberContext.getMember(), product.get());

        return "redirect:/product/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/remove/{productId}")
    public String removeItem(@AuthenticationPrincipal MemberContext memberContext, @PathVariable Long productId) {
        Product product = productService.findById(productId).get();
        Member member = memberContext.getMember();

        if(cartItemService.memberCanRemove(member, product) == false) {
            throw new ActorCanNotRemoveException();
        }

        cartItemService.remove(member, product);

        return Rq.redirectWithMsg("/cart/list", "%d번 상품이 삭제되었습니다.".formatted(product.getId()));
    }

}
