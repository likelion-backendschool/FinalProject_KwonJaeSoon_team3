package com.ll.ebook.app.order.repository;

import com.ll.ebook.app.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
