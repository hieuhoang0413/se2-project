package com.se2.midterm.controller;

import com.se2.midterm.entity.Product;
import com.se2.midterm.entity.Review;
import com.se2.midterm.entity.User;
import com.se2.midterm.service.ProductService;
import com.se2.midterm.service.ReviewService;
import com.se2.midterm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    // ReviewController.java
    @PostMapping("/add")
    public String addReview(@RequestParam Long productId,
                            @RequestParam Short rating,
                            @RequestParam String comment,
                            Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userService.findByUsername(principal.getName());
        Product product = productService.getProductById(productId);

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);

        reviewService.saveReview(review);

        return "redirect:/product/" + productId;
    }

}
