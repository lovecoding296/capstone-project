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
		System.out.println("Review received: " + reviewRequest);

		logger.info("userDetails " + userDetails);
		logger.info("book Id " +reviewRequest.getBooking().getId() + " reviewedId " + reviewRequest.getReviewedUser().getId());  
		
		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
		}
		reviewRequest.setReviewer(userDetails.getUser());
		

		return reviewService.addReview(reviewRequest);
		
		
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
