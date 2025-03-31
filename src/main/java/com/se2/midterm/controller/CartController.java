package com.se2.midterm.controller;

import com.se2.midterm.entity.Cart;
import com.se2.midterm.entity.User;
import com.se2.midterm.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    //API lấy giỏ hàng của người dùng
    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        return cartService.getOrCreateCart(user);
    }

    //API thêm sản phẩm vào giỏ hàng
    @PostMapping("/add")
    public Cart addToCart(@RequestParam Long userId, @RequestParam Long productId, @RequestParam int quantity) {
        User user = new User();
        user.setId(userId);
        return cartService.addToCart(user, productId, quantity);
    }

    //API xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/remove")
    public Cart removeFromCart(@RequestParam Long userId, @RequestParam Long cartItemId) {
        User user = new User();
        user.setId(userId);
        return cartService.removeFromCart(user, cartItemId);
    }

    //API cập nhật số lượng sản phẩm trong giỏ hàng
    @PutMapping("/update")
    public Cart updateCartItemQuantity(@RequestParam Long userId, @RequestParam Long cartItemId, @RequestParam int quantity) {
        User user = new User();
        user.setId(userId);
        return cartService.updateCartItemQuantity(user, cartItemId, quantity);
    }

    //API lấy tổng tiền giỏ hàng
    @GetMapping("/total/{userId}")
    public BigDecimal getTotalPrice(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        return cartService.getTotalPrice(user);
    }

    //API chuyển giỏ hàng sang trạng thái CHECKOUT
    @PostMapping("/checkout")
    public Cart checkout(@RequestParam Long userId) {
        User user = new User();
        user.setId(userId);
        return cartService.checkout(user);
    }

    @GetMapping("/view/{userId}")
    public String viewCartPage(@PathVariable Long userId, Model model) {
        User user = new User();
        user.setId(userId);

        Cart cart = cartService.getOrCreateCart(user);
        BigDecimal total = cartService.getTotalPrice(user);

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        model.addAttribute("userId", userId);

        return "cart"; // Trỏ đến templates/cart.html
    }

}

