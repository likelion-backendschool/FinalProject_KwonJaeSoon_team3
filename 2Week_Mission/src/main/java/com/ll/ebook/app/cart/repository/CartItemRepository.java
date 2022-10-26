package com.ll.ebook.app.cart.repository;


import com.ll.ebook.app.cart.entity.CartItem;
import com.ll.ebook.app.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByMemberId(Long id);

    Optional<CartItem> findByMemberIdAndProductId(Long memberId, Long productId);
}
