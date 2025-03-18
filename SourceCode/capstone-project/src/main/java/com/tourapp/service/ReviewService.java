package com.tourapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tourapp.entity.Review;
import com.tourapp.entity.Tour;
import com.tourapp.repository.ReviewRepository;

import jakarta.validation.Valid;

@Service
public class ReviewService {
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
    private TourService tourService;
	
	@Autowired
    private UserService userService;
	
	public List<Review> getReviewsByTour(Long tourId) {
	    return reviewRepository.findByTourId(tourId);
	}

	@Transactional
	public Review saveReview(@Valid Review review) {  		
		Tour tour = tourService.findById(review.getTour().getId());
		
		tourService.updateTourRating(review.getTour().getId(), review.getRating());		
		userService.updateGuideRating(tour.getGuide().getId(), review.getRating());
		return reviewRepository.save(review);
	}
	
	public List<Review> findTop3ByOrderByRating(){
		return reviewRepository.findTop3ByOrderByRating();
	}


}
