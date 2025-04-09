package com.se2.midterm.service;

import com.se2.midterm.entity.Product;
import com.se2.midterm.entity.Review;
import com.se2.midterm.entity.User;
import com.se2.midterm.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // khởi tạo @Mock
    }
    @Test
    void testGetReviewByProduct() {
        // Given
        Product product = new Product();
        product.setId(1L);

        Review review1 = new Review();
        review1.setId(100L);
        review1.setProduct(product);
        review1.setRating((short) 5);

        Review review2 = new Review();
        review2.setId(101L);
        review2.setProduct(product);
        review2.setRating((short) 4);

        List<Review> mockReviews = Arrays.asList(review1, review2);

        when(reviewRepository.findByProduct(product)).thenReturn(mockReviews);

        // When
        List<Review> result = reviewService.getReviewByProduct(product);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRating()).isEqualTo((short) 5);
        verify(reviewRepository, times(1)).findByProduct(product);
    }

    @Test
    void testSaveReview() {
        // Given
        Product product = new Product();
        product.setId(1L);

        User user = new User();
        user.setId(1L);

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating((short) 4);
        review.setComment("Nice product");

        when(reviewRepository.save(review)).thenReturn(review);

        // When
        Review saved = reviewService.saveReview(review);

        // Then
        assertThat(saved.getRating()).isEqualTo((short) 4);
        verify(reviewRepository, times(1)).save(review);
    }
}

