package com.se2.midterm.repository;

import com.se2.midterm.entity.Cart;
import com.se2.midterm.entity.CartItem;
import com.se2.midterm.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
