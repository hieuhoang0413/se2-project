package com.se2.midterm.controller;

import com.se2.midterm.entity.Product;
import com.se2.midterm.entity.User;
import com.se2.midterm.service.ProductService;
import com.se2.midterm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class BaseController {
    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

//    @GetMapping()
//    public String getGlobalPage(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//
//        // Fetch the current user's details
//        User currentUser = userService.findByUsername(username);
//
//        // Add user to the model to populate account details
//        model.addAttribute("user", currentUser);
//
//        return "_layout";
//    }

    @GetMapping("/")
    public String getHomePage() {
        return "index";
    }

    @GetMapping("/shop")
    public String getShopPage(Model model) {
        List<Product> products;
        products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "shop";
    }
}
