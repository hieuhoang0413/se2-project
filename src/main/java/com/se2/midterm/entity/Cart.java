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

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
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
    public double getTotalPrice() {
        totalPrice = 0.0;
        // Ensure the cartItems list is not null
        if (cartItems == null || cartItems.isEmpty()) {
            System.out.println("No items in cart to update total price.");
            return 0;
        }//cai subtotal dang ko nhan thi phai, trong dbs y choi ban luon
        for (CartItem item : cartItems) {
            totalPrice += item.getPrice()* item.getQuantity();
        }
        System.out.println("Updated total price: " + this.totalPrice);
        return totalPrice;
    }

    // Getter & Setter

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<CartItem> getCartItems() { return cartItems; }
/*    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        updateTotalPrice(); // Cập nhật tổng tiền khi có thay đổi
    }*/

    public void setStatus(CartStatus status) {
        this.status = status;
    }

    public CartStatus getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}