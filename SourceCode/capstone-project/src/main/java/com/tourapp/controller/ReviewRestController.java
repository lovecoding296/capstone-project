package com.tourapp.controller;

import com.tourapp.entity.AppUser;
import com.tourapp.entity.Review;
import com.tourapp.service.ReviewService;
import com.tourapp.service.UserService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewRestController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewRestController.class);

    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private UserService userService;

    /**
     * Get all reviews for a specific tour
     */
    @GetMapping("/{tourId}")
    public ResponseEntity<List<Review>> getReviewsByTour(@PathVariable Long tourId) {
        logger.info("Fetching reviews for tour ID: {}", tourId);
        
        List<Review> reviews = reviewService.getReviewsByTour(tourId);
        if (reviews.isEmpty()) {
            logger.info("No reviews found for tour ID: {}", tourId);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        }
        
        return ResponseEntity.ok(reviews); // HTTP 200 OK
    }

    /**
     * Add a new review for a tour
     */
    @PostMapping("/add")
    public ResponseEntity<?> addReview(@Valid @RequestBody Review review) {
        try {
        	AppUser user = userService.getCurrentAuthenticatedUser();
        	
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated"); // 401 Unauthorized
            }
            review.setAuthor(user);
            Review savedReview = reviewService.saveReview(review);
            logger.info("Review added by user ID {} for tour ID {}", review.getAuthor().getId(), review.getTour().getId());
            return ResponseEntity.ok(savedReview); // HTTP 200 OK

        } catch (Exception e) {
            logger.error("Error while adding review", e);
            return ResponseEntity.status(500).body("Internal Server Error"); // HTTP 500 Internal Server Error
        }
    }
}
