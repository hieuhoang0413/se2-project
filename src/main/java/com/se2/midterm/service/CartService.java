package com.se2.midterm.service;

import com.se2.midterm.entity.*;
import com.se2.midterm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    //Lấy giỏ hàng của người dùng, nếu chưa có thì tạo mới
    public Cart getOrCreateCart(User user) {
        Optional<Cart> cartOptional = cartRepository.findByUser(user);
        if (cartOptional.isPresent()) {
            return cartOptional.get();
        } else {
            Cart newCart = new Cart(user);
            return cartRepository.save(newCart);
        }
    }

    //Thêm sản phẩm vào giỏ hàng
    public Cart addToCart(User user, Long productId, int quantity) {
        Cart cart = getOrCreateCart(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        List<CartItem> cartItems = cart.getCartItems();
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                item.updateSubtotal();
                cartItemRepository.save(item);
                cart.updateTotalPrice();
                cartRepository.save(cart);
                return cart;
            }
        }

        // Nếu sản phẩm chưa có trong giỏ hàng, tạo mục mới
        CartItem newItem = new CartItem(cart, product, quantity);
        cartItemRepository.save(newItem);
        cart.getCartItems().add(newItem);
        cart.updateTotalPrice();
        cartRepository.save(cart);

        return cart;
    }

    //Xóa sản phẩm khỏi giỏ hàng
    public Cart removeFromCart(User user, Long cartItemId) {
        Cart cart = getOrCreateCart(user);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        cart.updateTotalPrice();
        cartRepository.save(cart);

        return cart;
    }

    //Cập nhật số lượng sản phẩm trong giỏ hàng
    public Cart updateCartItemQuantity(User user, Long cartItemId, int quantity) {
        Cart cart = getOrCreateCart(user);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        cartItem.setQuantity(quantity);
        cartItem.updateSubtotal();
        cartItemRepository.save(cartItem);
        cart.updateTotalPrice();
        cartRepository.save(cart);

        return cart;
    }

    //Tính tổng tiền giỏ hàng
    public double getTotalPrice(User user) {
        Cart cart = getOrCreateCart(user);
        return cart.getTotalPrice();
    }

    //Chuyển trạng thái giỏ hàng sang "CHECKOUT"
    public Cart checkout(User user) {
        Cart cart = getOrCreateCart(user);
        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }
        cart.setStatus(CartStatus.CHECKOUT);
        cartRepository.save(cart);
        return cart;
    }

    //Chuyển trạng thái giỏ hàng sang "COMPLETED" sau khi thanh toán thành công
    public Cart completeOrder(User user) {
        Cart cart = getOrCreateCart(user);
        if (cart.getStatus() != CartStatus.CHECKOUT) {
            throw new RuntimeException("Cart is not in checkout state!");
        }
        cart.setStatus(CartStatus.COMPLETED);
        cartRepository.save(cart);
        return cart;
    }
}
