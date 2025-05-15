package funix.tgcp.booking.review;

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
public class ReviewRestController {
	private static final LogHelper logger = new LogHelper(ReviewRestController.class);

	@Autowired
	private ReviewService reviewService;

	@PostMapping
	public ResponseEntity<String> submitReview(
	        @RequestBody Review reviewRequest,
	        @AuthenticationPrincipal CustomUserDetails auth) {
		
        reviewRequest.setReviewer(auth.getUser());
        reviewService.addReview(reviewRequest);
        return ResponseEntity.ok("Review submitted successfully!");
	}

	@GetMapping("/exists")
	public ResponseEntity<Boolean> hasReviewed(@RequestParam Long bookingId,
			@AuthenticationPrincipal CustomUserDetails auth) {

		boolean exists = reviewService.existsByReviewerIdAndBookingId(auth.getId(), bookingId);
		return ResponseEntity.ok(exists);

	}
	
	

}
