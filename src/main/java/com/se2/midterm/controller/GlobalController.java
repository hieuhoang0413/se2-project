package com.se2.midterm.controller;

import com.se2.midterm.entity.Cart;
import com.se2.midterm.entity.User;
import com.se2.midterm.service.CartService;
import com.se2.midterm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalController {
    @Autowired
    private UserService userService; // Service lấy thông tin user từ DB

    @Autowired
    private CartService cartService; // Service quản lý giỏ hàng

    @ModelAttribute
    public void addUserToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername()); // Lấy user từ DB

            if (user != null) {
                Cart cart = user.getCart(); // Fetch or create the cart for the user
                int cartItemCount = cart.getCartItems().size(); // Get the count of cart items
                model.addAttribute("cartItemCount", cartItemCount); // Add cart item count to model
            }

            model.addAttribute("user", user);
        }
    }
}
