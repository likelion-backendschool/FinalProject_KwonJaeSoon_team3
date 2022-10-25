package com.ll.ebook.app.order.service;

import com.ll.ebook.app.cart.entity.CartItem;
import com.ll.ebook.app.cart.service.CartItemService;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.member.service.MemberService;
import com.ll.ebook.app.order.entity.Order;
import com.ll.ebook.app.order.entity.OrderItem;
import com.ll.ebook.app.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartItemService cartItemService;
    private final OrderRepository orderRepository;
    private final MemberService memberService;

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

        order.makeName();

        orderRepository.save(order);

        return order;
    }

    public void orderByRestCash(Order order) {
        Member member = order.getMember();
        long restCash = member.getRestCash();

        int payPrice = order.calculatePayPrice();

        order.setPaymentDone();
        memberService.addCash(member, payPrice * -1, "주문결제__예치금결제");
        orderRepository.save(order);
    }

    public List<Order> findAllByMemberId(Long memberId) {
        return orderRepository.findAllByMemberId(memberId);
    }

    public Optional<Order> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public void delete(Order order) {
        orderRepository.delete(order);
    }

    public Optional<Order> findForPrintById(long id) {
        return findById(id);
    }

    private Optional<Order> findById(long id) {
        return orderRepository.findById(id);
    }

    public void payByTossPayments(Order order, long useRestCash) {
        Member member = order.getMember();
        int payPrice = order.calculatePayPrice();

        long pgPayPrice = payPrice - useRestCash;

        memberService.addCash(member, pgPayPrice, "주문결제충전__토스페이먼츠");
        memberService.addCash(member, pgPayPrice * -1, "주문결제__토스페이먼츠");

        if ( useRestCash > 0 ) {
            memberService.addCash(member, useRestCash * -1, "주문__%d__사용__예치금".formatted(order.getId()));
        }

        order.setPaymentDone();
        orderRepository.save(order);
    }

    public boolean actorCanPayment(Member member, Order order) {
        return actorCanSee(member, order);
    }

    private boolean actorCanSee(Member member, Order order) {
        return member.getId().equals(order.getMember().getId());
    }

    public void payByRestCashOnly(Order order) {
        Member member = order.getMember();
        long restCash = member.getRestCash();
        int payPrice = order.calculatePayPrice();
        if (payPrice > restCash) {
            throw new RuntimeException("예치금이 부족합니다.");
        }
        memberService.addCash(member, payPrice * -1, "주문결제__예치금결제");
        order.setPaymentDone();
        orderRepository.save(order);
    }
}
