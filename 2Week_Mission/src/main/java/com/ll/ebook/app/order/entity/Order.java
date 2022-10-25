package com.ll.ebook.app.order.entity;

import com.ll.ebook.app.base.entity.BaseEntity;
import com.ll.ebook.app.member.entity.Member;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;


import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Table(name = "product_order")
public class Order extends BaseEntity {
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @CreatedDate
    private LocalDateTime payDate;
    private boolean readyStatus;
    private boolean isPaid;
    private boolean isCanceled;
    private boolean isRefunded;
    private String name;
    private int totalPrice;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItem orderItem) {
        orderItem.setOrder(this);

        orderItems.add(orderItem);
    }

    public int calculatePayPrice() {
        int payPrice = 0;

        for ( OrderItem orderItem : orderItems ) {
            payPrice += orderItem.calculatePayPrice();
        }

        this.totalPrice = payPrice;

        return payPrice;
    }

    public void setPaymentDone() {
        for ( OrderItem orderItem : orderItems ) {
            orderItem.setPaymentDone();
        }
        isPaid = true;
    }

    public void makeName() {
        String name = orderItems.get(0).getProduct().getSubject();

        if (orderItems.size() > 1) {
            name += " 외 %d개".formatted(orderItems.size() - 1);

            this.name = name;
        }
    }

}
