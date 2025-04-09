package com.se2.midterm.entity;

import jakarta.persistence.*;


import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
public class Cart {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id") // đây là user_id luôn
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    private double totalPrice;
    private CartStatus status;

    public Cart() {
        this.totalPrice = 0.0;
    }

    public Cart(User user) {
        this.user = user;
        this.cartItems = new ArrayList<>();
        this.totalPrice = 0.0;
    }

    // Tính tổng tiền từ CartItems
    public void updateTotalPrice() {
        // Ensure the cartItems list is not null
        if (cartItems == null || cartItems.isEmpty()) {
            System.out.println("No items in cart to update total price.");
            return;
        }
        double total = 0;
        int totalQuantity = 0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
            totalQuantity += item.getQuantity();
        }
        this.totalPrice = total;
        // Print the updated total price for debugging
        System.out.println("Updated total price: " + this.totalPrice);

    }


    // Getter & Setter
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
        updateTotalPrice();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setStatus(CartStatus status) {
        this.status = status;
    }

    public CartStatus getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}