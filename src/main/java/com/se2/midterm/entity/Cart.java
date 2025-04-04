package com.se2.midterm.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
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

    private BigDecimal totalPrice;
    private CartStatus status;

    public Cart() {
        this.totalPrice = BigDecimal.valueOf(0.0);
    }

    public Cart(User user) {
        this.user = user;
        this.totalPrice = BigDecimal.valueOf(0.0);
    }

    // Tính tổng tiền từ CartItems
    public void updateTotalPrice() {
        this.totalPrice = BigDecimal.valueOf(cartItems.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum());
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

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = BigDecimal.valueOf(totalPrice); }

    public void setStatus(CartStatus status) {
        this.status = status;
    }

    public CartStatus getStatus() {
        return status;
    }
}