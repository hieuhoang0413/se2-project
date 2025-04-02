package com.se2.midterm.controller;

import com.se2.midterm.entity.Order;
import com.se2.midterm.entity.OrderStatus;
import com.se2.midterm.repository.OrderRepository;
import com.se2.midterm.repository.OrderStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    // 1. Retrieve all orders
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 2. Retrieve a single order by ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        return orderOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Create a new order
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        // Example: set a default status to PENDING (requires a findByStatus method)
        OrderStatus pendingStatus = orderStatusRepository.findByStatus(OrderStatus.Status.PENDING);
        order.setStatus(pendingStatus);

        // Set the order date to now
        order.setOrderDate(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        return ResponseEntity.ok(savedOrder);
    }

    // 4. Update an existing order
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order updatedOrder) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Order existingOrder = orderOpt.get();

        // The following setters must match fields in your Order entity
        existingOrder.setUser(updatedOrder.getUser());           // Remove if you don't have a User field
        existingOrder.setStatus(updatedOrder.getStatus());
        existingOrder.setOrderDate(updatedOrder.getOrderDate());
        existingOrder.setTotalAmount(updatedOrder.getTotalAmount());

        Order savedOrder = orderRepository.save(existingOrder);
        return ResponseEntity.ok(savedOrder);
    }

    // 5. Delete an order
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        orderRepository.delete(orderOpt.get());
        return ResponseEntity.noContent().build();
    }
}
