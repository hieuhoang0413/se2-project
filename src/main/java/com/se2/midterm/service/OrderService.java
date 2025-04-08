package com.se2.midterm.service;

import com.se2.midterm.entity.Order;
import com.se2.midterm.entity.OrderStatus;
import com.se2.midterm.entity.User;
import com.se2.midterm.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
    public List<Order> findOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }
    public Order save(Order order) {
        return orderRepository.save(order);
    }
    public Page<Order> getAllOrdersPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        return orderRepository.findAll(pageable);
    }

}
