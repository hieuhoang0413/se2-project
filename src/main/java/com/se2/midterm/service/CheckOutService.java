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

    /**
     * Processes the checkout for a given user's cart.
     * It retrieves the cart, calculates the total, creates an Order and OrderDetail records,
     * then updates the cart status to COMPLETE.
     *
     * @param userId the id of the user checking out.
     * @return the created Order.
     */
    public Order checkoutCart(Long userId) {
        // Retrieve the user's active cart.
        // Assume there's a method to find a cart by user and status 'CHECKOUT'
        Optional<Cart> cartOptional = cartRepository.findByUserIdAndStatus(userId, CartStatus.CHECKOUT);
        if (cartOptional.isEmpty()) {
            throw new RuntimeException("No active checkout cart found for user id " + userId);
        }
        Cart cart = cartOptional.get();
        Order order = new Order();
        order.setUser(cart.getUser());

        // Set default order status to PENDING.
        OrderStatus pendingStatus = orderStatusRepository.findByStatus(OrderStatus.Status.PENDING);
        order.setStatus(pendingStatus);

        // Set the order date to current time.
        order.setOrderDate(LocalDateTime.now());

        // Calculate the total amount from cart items.
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        double totalAmount = 0.0;
        for (CartItem item : cartItems) {
            double itemTotal = item.getProduct().getPrice() * item.getQuantity();
            totalAmount += itemTotal;
        }
        order.setTotalAmount(totalAmount);

        // Save the Order to generate its ID.
        Order savedOrder = orderRepository.save(order);

        // Create OrderDetail for each CartItem.
        for (CartItem item : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(savedOrder);
            orderDetail.setProduct(item.getProduct());
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setPrice(item.getProduct().getPrice());
            // If you use an OrderHistory mechanism, set it accordingly; otherwise, omit.
            orderDetailRepository.save(orderDetail);
        }

        // Update the cart status to COMPLETE (or clear the cart) to mark that it has been processed.
        cart.setStatus(CartStatus.COMPLETED);
        cartRepository.save(cart);

        return savedOrder;
    }
}
