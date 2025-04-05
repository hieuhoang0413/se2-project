package com.se2.midterm.repository;

import com.se2.midterm.entity.Order;
import com.se2.midterm.entity.OrderStatus;
import com.se2.midterm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByOrderByOrderDateDesc();
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByUser(User user);
}
