package com.se2.midterm.repository;

import com.se2.midterm.entity.Cart;
import com.se2.midterm.entity.CartStatus;
import com.se2.midterm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
    // If you have a CartStatus enum, and 'user' is a field of type User in your Cart entity:
    Optional<Cart> findByUserAndStatus(User user, CartStatus status);

    // Or, if you prefer to look up by userId (assuming 'user' is a ManyToOne relationship):
    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);
}