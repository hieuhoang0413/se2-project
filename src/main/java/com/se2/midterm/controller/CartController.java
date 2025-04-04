package com.se2.midterm.controller;

import com.se2.midterm.entity.Cart;
import com.se2.midterm.entity.Order;
import com.se2.midterm.entity.User;
import com.se2.midterm.service.CartService;
import com.se2.midterm.service.CheckOutService;
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
    @Autowired
    private CheckOutService CheckOutService;

    //API lấy giỏ hàng của người dùng
    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        return cartService.getOrCreateCart(user);
    }


    @PostMapping("/add")
    public String addToCart(@RequestParam Long userId,
                          @RequestParam Long productId,
                          @RequestParam int quantity) {
        User user = new User();
        user.setId(userId);
         cartService.addToCart(user, productId, quantity);
        return "redirect:/product/" + productId;

    }

    //API xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/remove")
    public Cart removeFromCart(@RequestParam Long userId,
                               @RequestParam Long cartItemId) {
        User user = new User();
        user.setId(userId);
        return cartService.removeFromCart(user, cartItemId);
    }

    //API cập nhật số lượng sản phẩm trong giỏ hàng
    @PutMapping("/update")
    public Cart updateCartItemQuantity(@RequestParam Long userId,
                                       @RequestParam Long cartItemId,
                                       @RequestParam int quantity) {
        User user = new User();
        user.setId(userId);
        return cartService.updateCartItemQuantity(user, cartItemId, quantity);
    }

    //API lấy tổng tiền giỏ hàng
    @GetMapping("/total/{userId}")
    public double getTotalPrice(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        return cartService.getTotalPrice(user);
    }

    //API chuyển giỏ hàng sang trạng thái CHECKOUT
    @PostMapping("/checkout")
    public Order checkout(@RequestParam Long userId) {
        return CheckOutService.checkoutCart(userId);
    }

    @GetMapping("/view/{userId}")
    public String viewCartPage(@PathVariable Long userId, Model model) {
        User user = new User();
        user.setId(userId);

        Cart cart = cartService.getOrCreateCart(user);
        double total = cartService.getTotalPrice(user);

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        model.addAttribute("userId", userId);

        return "cart"; // Trỏ đến templates/cart.html
    }

}

