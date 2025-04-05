package com.se2.midterm.entity;

import jakarta.persistence.*;


import java.util.ArrayList;
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

        // Print the number of items in the cart to help debug
        System.out.println("Updating total price for " + cartItems.size() + " items...");

        // Sum up the subtotals of each cart item
        this.totalPrice = cartItems.stream()
                .filter(item -> item != null && item.getSubtotal() > 0)  // Make sure item is not null and has a positive subtotal
                .mapToDouble(CartItem::getSubtotal)
                .sum();

        // Print the updated total price for debugging
        System.out.println("Updated total price: " + this.totalPrice);

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

    public void setStatus(CartStatus status) {
        this.status = status;
    }

    public CartStatus getStatus() {
        return status;
    }
}