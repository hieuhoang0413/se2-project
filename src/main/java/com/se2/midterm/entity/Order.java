package com.se2.midterm.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")  // Adjust if your table is named differently
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // If you want a relationship to a User entity:
    @ManyToOne
    @JoinColumn(name = "user_id") // Must match the column in your 'orders' table
    private User user;

    // If you want a relationship to an OrderStatus entity:
    @ManyToOne
    @JoinColumn(name = "status_id") // Must match the column in your 'orders' table
    private OrderStatus status;

    private LocalDateTime orderDate;

    private double totalAmount;

    // --- GETTERS & SETTERS (manually defined) ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

}
