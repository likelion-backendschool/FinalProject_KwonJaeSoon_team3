package com.ll.ebook.app.cart.service;

import com.ll.ebook.app.cart.entity.CartItem;
import com.ll.ebook.app.cart.repository.CartItemRepository;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;

    public CartItem addItem(Member member, Product product) {
        CartItem cartItem = CartItem.builder()
                .member(member)
                .product(product)
                .build();

        cartItemRepository.save(cartItem);

        return cartItem;
    }
    public List<CartItem> findAllByMemberId(Long id) {
        return cartItemRepository.findAllByMemberId(id);
    }

    public List<CartItem> findAll() {
        return cartItemRepository.findAll();
    }
}
