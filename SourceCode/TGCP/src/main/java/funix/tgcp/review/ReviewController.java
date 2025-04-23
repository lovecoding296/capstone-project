package funix.tgcp.review;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.notification.NotificationService;
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
	public ResponseEntity<String> submitReview(@RequestBody Review reviewRequest) {
		System.out.println("Review received: " + reviewRequest);

		CustomUserDetails userDetails = CustomUserDetails.getCurrentUserDetails();
		logger.info("userDetails " + userDetails);
		logger.info("book Id " +reviewRequest.getBooking().getId() + " reviewedId " + reviewRequest.getReviewedUser().getId());  
		
		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
		}
		reviewRequest.setReviewer(userDetails.getUser());
		

		return reviewService.addReview(reviewRequest);
		
		
	}
	
	@GetMapping("/exists")
	public ResponseEntity<Boolean> hasReviewed(@RequestParam Long bookingId) {
		CustomUserDetails userDetails = CustomUserDetails.getCurrentUserDetails();
		if(userDetails != null) {
			boolean exists = reviewService.existsByReviewerIdAndBookingId(userDetails.getUser().getId(), bookingId);
			return ResponseEntity.ok(exists);
		}
		
		return ResponseEntity.ok(false);
	}
	
	

}
