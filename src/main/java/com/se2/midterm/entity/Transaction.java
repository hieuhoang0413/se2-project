package com.se2.midterm.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orderId", referencedColumnName = "id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "paymentId", referencedColumnName = "id")
    private Payment payment;

    private double totalAmount;

    // Getters and Setters
}

