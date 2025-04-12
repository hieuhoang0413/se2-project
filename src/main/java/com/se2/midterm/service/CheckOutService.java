package com.se2.midterm.service;

import com.se2.midterm.entity.*;
import com.se2.midterm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckOutService {
    @Autowired
    private UserRepository userRepository;

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

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

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
        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty or not found!");
        }

        // Tạo Order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setCode(generateRandomCode(8));

        // Gán trạng thái mặc định (nếu có OrderStatus entity)

        order.setStatus(orderStatusRepository.findByStatus(OrderStatus.Status.PENDING));
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
        return order;
    }

    public String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}
