package com.ll.ebook.app.order.service;

import com.ll.ebook.app.cart.entity.CartItem;
import com.ll.ebook.app.cart.service.CartItemService;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.order.entity.Order;
import com.ll.ebook.app.order.entity.OrderItem;
import com.ll.ebook.app.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartItemService cartItemService;
    private final OrderRepository orderRepository;

    public Order createFromCart(Member member) {
        List<CartItem> cartItems = cartItemService.getItemsByMember(member);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            orderItems.add(new OrderItem(cartItem.getProduct()));

            cartItemService.deleteItem(cartItem);
        }

        return create(member, orderItems);
    }

    public Order create(Member member, List<OrderItem> orderItems) {
        Order order = Order
                .builder()
                .member(member)
                .build();

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        orderRepository.save(order);

        return order;
    }

    public void order(Order order) {
        order.setPaymentDone();
        orderRepository.save(order);
    }
}
