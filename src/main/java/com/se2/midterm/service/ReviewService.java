package com.se2.midterm.service;

import com.se2.midterm.entity.Product;
import com.se2.midterm.entity.Review;
import com.se2.midterm.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> getReviewByProduct(Product product) {
        return reviewRepository.findByProduct(product);
    }


    public Review saveReview(Review review){
        return reviewRepository.save(review);
    }
}
