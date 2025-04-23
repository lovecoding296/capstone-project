package funix.tgcp.review;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import funix.tgcp.booking.Booking;
import funix.tgcp.booking.BookingService;
import funix.tgcp.notification.NotificationService;
import funix.tgcp.user.User;
import funix.tgcp.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	
	long countByReviewedUser(User reviewedUser) {
		return reviewRepository.countByReviewedUser(reviewedUser);
	}
	
    Double findAverageRatingByReviewedUser(User user) {
    	return reviewRepository.findAverageRatingByReviewedUser(user);
    }

    
    public List<Review> findByReviewedUserIdOrderByRatingDesc(Long reviewedUserId){
    	return reviewRepository.findByReviewedUserIdOrderByRatingDesc(reviewedUserId);
    }

    // Lưu một đánh giá
    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    // Lấy một đánh giá theo ID
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    // Lấy tất cả các đánh giá
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    // Lấy các đánh giá của một người dùng
    public List<Review> findByReviewedUserId(Long reviewedUserId) {
        return reviewRepository.findByReviewedUserId(reviewedUserId);
    }

    // Lấy các đánh giá của một người thực hiện
    public List<Review> findByReviewerId(Long reviewerId) {
        return reviewRepository.findByReviewerId(reviewerId);
    }
    
    public List<Review> findByBookingId(Long bookingId) {
        return reviewRepository.findByBookingId(bookingId);
    }

    // Cập nhật đánh giá
    public Review update(Review review) {
        return reviewRepository.save(review);
    }

    // Xóa đánh giá theo ID
    @Transactional
    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }
    
    List<Review> findByBookingIdAndReviewerId(Long bookingId, Long reviewer){
    	return reviewRepository.findByBookingIdAndReviewerId(bookingId, reviewer);
    }
    
    /**
     * Kiểm tra xem một người dùng đã đánh giá một người khác trong Booking hay chưa
     */
    public boolean hasUserReviewed(Long bookingId, Long reviewerId, Long reviewedUserId) {
        return reviewRepository.existsByBookingIdAndReviewerIdAndReviewedUserId(bookingId, reviewerId, reviewedUserId);
    }
    
    /**
     * Chỉnh sửa đánh giá
     */
    @Transactional
    public void updateReview(Long reviewId, int rating, String feedback) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Đánh giá không tồn tại."));
        
        review.setRating(rating);
        review.setFeedback(feedback);
        reviewRepository.save(review);
    }

	public Optional<Review> getReviewById(Long reviewId) {
		// TODO Auto-generated method stub
		return reviewRepository.getReviewById(reviewId);
	}

	@Transactional
	public ResponseEntity<String> addReview(Review reviewRequest) {

		Optional<Booking> bOp = bookingService.findById(reviewRequest.getBooking().getId());
		Optional<User> reviewedUserOp = userService.findById(reviewRequest.getReviewedUser().getId());

		if (bOp.isEmpty() || reviewedUserOp.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid booking or reviewed user ID");
		}

		Booking b = bOp.get();
		User reviewedUser = reviewedUserOp.get();

		ArrayList<User> participants = new ArrayList<User>();
		participants.add(b.getCustomer());
		participants.add(b.getGuide());

		if (participants.contains(reviewedUser) && participants.contains(reviewRequest.getReviewer())) {
			reviewRepository.save(reviewRequest);
			
			userService.updateRating(reviewedUser, reviewRequest.getRating());
			
			notificationService.sendNotification(
					reviewRequest.getReviewedUser(),
					reviewRequest.getReviewer().getFullName() + " rated you " + reviewRequest.getRating() + " stars",
	    			"/users/" + reviewRequest.getReviewedUser().getId());
			
			return ResponseEntity.ok("Review submitted successfully");
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to review this user");
		}
	}

	public List<Review> getAllReviews() {
		return reviewRepository.findAll();
	}

	public Optional<Review> findByBookingIdAndReviewedUserId(Long bookingId, Long reviewedUserId) {		
		return reviewRepository.findByBookingIdAndReviewedUserId(bookingId, reviewedUserId);
	}
}
