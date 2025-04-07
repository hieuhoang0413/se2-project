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
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long userId,
                               @RequestParam Long cartItemId) {
        User user = new User();
        user.setId(userId);
        cartService.removeFromCart(user, cartItemId);

        return "redirect:/cart/view/" + userId;
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
    @GetMapping("/checkout")
    public String checkout(@RequestParam(required = false) Long userId, Model model) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        User user = new User();
        user.setId(userId);
        Cart cart = cartService.getOrCreateCart(user);

        model.addAttribute("cart", cart);
        model.addAttribute("total", cart.getTotalPrice());
        model.addAttribute("userId", userId);
        return "checkout"; // Trỏ đến templates/checkout.html
    }

    //API xác nhận thanh toán
    @RequestMapping(value = "/checkout/confirm", method = {RequestMethod.GET, RequestMethod.POST})
    public String confirmCheckout(@RequestParam Long userId, Model model) {
        Order order = CheckOutService.checkout(userId);
        model.addAttribute("order", order);
        return "orderComplete";
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

