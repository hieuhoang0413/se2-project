package com.se2.midterm.service;

import com.se2.midterm.entity.*;
import com.se2.midterm.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderDetailRepository orderDetailRepository;
    @Autowired private OrderStatusRepository orderStatusRepository;

    // Lấy hoặc tạo giỏ hàng
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(new Cart(user)));
    }

    // Thêm vào giỏ hàng
    public void addToCart(User user, Long productId, int quantity) {
        Cart cart = getOrCreateCart(user);
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        Product product = optionalProduct.get();

        Optional<CartItem> optionalCartItem = cartItemRepository.findByCartAndProduct(cart, product);
        CartItem cartItem;
        if (optionalCartItem.isPresent()) {
            cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(product.getPrice());
        }
        cartItemRepository.save(cartItem);

        cart = cartRepository.findById(cart.getId()).orElse(cart);
        cart.getTotalPrice();
        cartRepository.save(cart);
    }

    // Xóa sản phẩm khỏi giỏ hàng
    public Cart removeFromCart(User user, Long cartItemId) {
        Cart cart = getOrCreateCart(user);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        cart.getTotalPrice();
        return cartRepository.save(cart);
    }

    // Cập nhật số lượng
    public Cart updateCartItemQuantity(User user, Long cartItemId, int quantity) {
        Cart cart = getOrCreateCart(user);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
        cartItem.setQuantity(quantity);
        cartItem.updateSubtotal();
        cart.getTotalPrice();
        cartItemRepository.save(cartItem);
        return cartRepository.save(cart);
    }

    // Lấy tổng tiền
    public double getTotalPrice(User user) {
        return getOrCreateCart(user).getTotalPrice();
    }

/*    @Transactional
    public Cart checkout(User user) {
        Cart cart = getOrCreateCart(user);
        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }

        // ➤ Tạo Order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalPrice());

        // Trạng thái mặc định
        OrderStatus status = orderStatusRepository.findByStatus(OrderStatus.Status.PENDING);
        order.setStatus(status);

        order = orderRepository.save(order); // ✅ Dòng này là quan trọng nhất
        System.out.println("✅ Đã lưu đơn hàng ID = " + order.getId());

        // ➤ Lưu chi tiết đơn hàng
        for (CartItem item : cart.getCartItems()) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getProduct().getPrice());
            orderDetailRepository.save(detail);
        }

        // ➤ Clear giỏ hàng
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cart.updateTotalPrice();
        cartRepository.save(cart);

        return cart;
    }*/
}
