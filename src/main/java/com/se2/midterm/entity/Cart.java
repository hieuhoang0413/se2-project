package com.se2.midterm.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> cartItems;

    private double totalPrice;

    @Enumerated(EnumType.STRING)
    private CartStatus status;  // Trạng thái giỏ hàng

    public Cart() {
        this.status = CartStatus.CART;
        this.totalPrice = 0.0;
    }

    public Cart(User user) {
        this.user = user;
        this.status = CartStatus.CART;
        this.totalPrice = 0.0;
    }

    // Tính tổng tiền từ CartItems
    public void updateTotalPrice() {
        this.totalPrice = cartItems.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<CartItem> getCartItems() { return cartItems; }
    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        updateTotalPrice(); // Cập nhật tổng tiền khi có thay đổi
    }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public CartStatus getStatus() { return status; }
    public void setStatus(CartStatus status) { this.status = status; }
}