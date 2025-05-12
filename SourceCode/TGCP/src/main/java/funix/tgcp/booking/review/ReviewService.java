package funix.tgcp.booking.review;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import funix.tgcp.booking.Booking;
import funix.tgcp.booking.BookingService;
import funix.tgcp.notification.NotificationService;
import funix.tgcp.user.User;
import funix.tgcp.user.UserService;
import jakarta.transaction.Transactional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;    
    
    @Autowired
    private  UserService userService;

	@Autowired
	private BookingService bookingService;
	
	@Autowired
	private NotificationService notificationService;
	
    
    public List<Review> findByReviewedUserIdOrderByRatingDesc(Long reviewedUserId){
    	return reviewRepository.findByReviewedUserIdOrderByRatingDesc(reviewedUserId);
    }

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

	@Transactional
	public void addReview(Review reviewRequest) {
	    Long bookingId = reviewRequest.getBooking().getId();
	    Long reviewedUserId = reviewRequest.getReviewedUser().getId();

	    Booking booking = bookingService.findById(bookingId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid booking ID"));

	    User reviewedUser = userService.findById(reviewedUserId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid reviewed user ID"));

	    User reviewer = reviewRequest.getReviewer();

	    if (!booking.getCustomer().equals(reviewer)) {
	        throw new SecurityException("You are not allowed to review this user");
	    }
	    
	    boolean alreadyReviewed = reviewRepository.existsByReviewerIdAndBookingId(bookingId, reviewer.getId());
	    if (alreadyReviewed) {
	        throw new IllegalStateException("You have already reviewed this booking");
	    }

	    reviewRequest.setReviewedUser(reviewedUser);
	    reviewRepository.save(reviewRequest);

	    userService.updateRating(reviewedUser, reviewRequest.getRating());

	    notificationService.sendNotification(
	        reviewedUser,
	        String.format("%s rated you %d stars", reviewer.getFullName(), reviewRequest.getRating()),
	        "/users/" + reviewedUser.getId()
	    );
	}

	public boolean existsByReviewerIdAndBookingId(Long reviewerId, Long bookingId) {
		return reviewRepository.existsByReviewerIdAndBookingId(reviewerId, bookingId);
	}
}
