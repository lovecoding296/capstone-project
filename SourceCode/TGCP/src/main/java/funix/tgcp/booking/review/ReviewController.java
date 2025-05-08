package funix.tgcp.booking.review;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.util.LogHelper;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
	private static final LogHelper logger = new LogHelper(ReviewController.class);

	@Autowired
	private ReviewService reviewService;

	
	
	
	@GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

	@PostMapping
	public ResponseEntity<String> submitReview(
	        @RequestBody Review reviewRequest,
	        @AuthenticationPrincipal CustomUserDetails userDetails) {

	    logger.info("Review received: {}", reviewRequest);
	    logger.info("userDetails: {}", userDetails);
	    logger.info("bookId: {}, reviewedId: {}", 
	                reviewRequest.getBooking().getId(), 
	                reviewRequest.getReviewedUser().getId());

	    if (userDetails == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
	    }

	    try {
	        reviewRequest.setReviewer(userDetails.getUser());
	        reviewService.addReview(reviewRequest);
	        return ResponseEntity.ok("Review submitted successfully!");

	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body(e.getMessage());

	    } catch (SecurityException e) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

	    } catch (IllegalStateException e) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

	    } catch (Exception e) {
	        logger.error("Unexpected error when submitting review", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
	    }
	}

	@GetMapping("/exists")
	public ResponseEntity<Boolean> hasReviewed(
			@RequestParam Long bookingId, 
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		if(userDetails != null) {
			boolean exists = reviewService.existsByReviewerIdAndBookingId(userDetails.getUser().getId(), bookingId);
			return ResponseEntity.ok(exists);
		}
		
		return ResponseEntity.ok(false);
	}
	
	

}
