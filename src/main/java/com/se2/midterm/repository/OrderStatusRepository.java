package com.se2.midterm.repository;

import com.se2.midterm.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {
    OrderStatus findByStatus(OrderStatus.Status status);
}