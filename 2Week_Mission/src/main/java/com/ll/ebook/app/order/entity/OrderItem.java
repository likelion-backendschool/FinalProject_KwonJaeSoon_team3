package com.ll.ebook.app.order.entity;

import com.ll.ebook.app.base.entity.BaseEntity;
import com.ll.ebook.app.product.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class OrderItem extends BaseEntity {
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Order order;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    @CreatedDate
    private LocalDate payDate;
    private int price;
    private int salePrice;
    private int wholesalePrice;
    private int pgFee;
    private int payPrice;
    private int refundPrice;
    private boolean isPaid;

    public OrderItem(Product product) {
        this.product = product;
        this.price = product.getPrice();
        this.salePrice = product.getSalePrice();
        this.wholesalePrice = (int)Math.ceil(product.getPrice() * 0.8) / 100 * 100;
    }

    public int calculatePayPrice() {
        return salePrice;
    }

    public void setPaymentDone() {
        this.pgFee = 0;
        this.payPrice = calculatePayPrice();
        this.isPaid = true;
    }

}
