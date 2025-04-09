package com.se2.midterm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_item")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;
    private double price;
    private double subtotal;

    public CartItem() {}

    public CartItem(Cart cart, Product product, int quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.price = product.getPrice();
        this.subtotal = this.price * this.quantity;
    }

    // Cập nhật lại tổng phụ khi số lượng thay đổi
    public void updateSubtotal() {
        this.subtotal = this.price * this.quantity;
/*        this.cart.getTotalPrice();  // Cập nhật tổng tiền giỏ hàng*/
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateSubtotal();
    }

    public double getPrice() { return price; }
    public double getSubtotal() { return subtotal; }

    public void setPrice(double price) {
        this.price = price;
    }
}