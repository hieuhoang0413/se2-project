package com.se2.midterm.service;

import com.se2.midterm.entity.*;
import com.se2.midterm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CheckOutService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private CartService cartService;

    /**
     * Processes the checkout for a given user's cart.
     * It retrieves the cart, calculates the total, creates an Order and OrderDetail records,
     * then updates the cart status to COMPLETE.
     *
     * @param userId the id of the user checking out.
     * @return the created Order.
     */
    // ✅ Tạo order mới khi checkout
    public Order checkout(Long userId) {
        // Khởi tạo đối tượng User chỉ với id
        User user = new User();
        user.setId(userId);

        // Lấy giỏ hàng của người dùng
        Cart cart = cartService.getOrCreateCart(user);
        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }

        // Tạo Order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalPrice());

        // Gán trạng thái mặc định (nếu có OrderStatus entity)
        OrderStatus status = orderStatusRepository.findByStatus(OrderStatus.Status.PENDING);
        order.setStatus(status);
        order = orderRepository.save(order);

        // Tạo OrderDetail cho từng item
        for (CartItem item : cart.getCartItems()) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getProduct().getPrice());
            orderDetailRepository.save(detail);
        }

        // Dọn sạch cart
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cart.updateTotalPrice();
        cartRepository.save(cart);

        return order;
    }
}
