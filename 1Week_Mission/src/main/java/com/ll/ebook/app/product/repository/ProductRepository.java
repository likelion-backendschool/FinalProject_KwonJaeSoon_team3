package com.ll.ebook.app.product.repository;

import com.ll.ebook.app.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
