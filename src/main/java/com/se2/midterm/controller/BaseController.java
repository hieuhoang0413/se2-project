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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/")
public class BaseController {
    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String getHomePage(Model model) {
        List<Product> allProducts = productService.getAllProducts();
        int limit = 4;
        List<Product> limitedProducts = allProducts.size() > limit ? allProducts.subList(0, limit) : allProducts;

        model.addAttribute("products", limitedProducts);
        return "index";
    }

    @GetMapping("/shop")
    public String getShopPage(Model model) {
        List<Product> products;
        products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "shop";
    }

    @GetMapping("/about")
    public String aboutPage() {
        return "aboutUs";
    }

    @GetMapping("/contact")
    public String contactPage() {
        return "contact";
    }

    @PostMapping("/contact")
    public String handleContactForm(@RequestParam String name,
                                    @RequestParam String email,
                                    @RequestParam String message,
                                    RedirectAttributes redirectAttributes) {
        System.out.println("📩 Contact from " + name + " (" + email + "): " + message);
        redirectAttributes.addFlashAttribute("successMessage", "Thanks for contacting us!");
        return "redirect:/contact";
    }
}
