package com.se2.midterm.service;

import com.se2.midterm.entity.*;
import com.se2.midterm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        // Dọn sạch cart


        return order;
    }

    public void clearOrderedCartItems(List<OrderDetail> orderDetails) {
        // Retrieve the authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        // Retrieve the user's cart using the userId
        Cart userCart = cartRepository.findByUser(user)
                .orElse(null);

        // Loop through each ordered item
        for (OrderDetail orderItem : orderDetails) {
            // Get the product variant from the order item
            Product product = orderItem.getProduct();
            if (product != null) {
                // Retrieve all cart items in the user's cart that correspond to this product variant
                List<CartItem> cartItems = cartItemRepository.findByProductAndCart(product, userCart);

                // The quantity ordered for the variant
                int orderedQuantity = orderItem.getQuantity();

                // Process each cart item individually
                for (CartItem cartItem : cartItems) {
                    int cartQuantity = cartItem.getQuantity();

                    // If the cart quantity is greater than the ordered quantity, simply update the quantity
                    if (cartQuantity > orderedQuantity) {
                        cartItem.setQuantity(cartQuantity - orderedQuantity);
                        cartItemRepository.save(cartItem);
                    } else {
                        // Otherwise, delete the cart item entirely
                        cartItemRepository.delete(cartItem);
                    }
                }
            }
        }
    }

}
