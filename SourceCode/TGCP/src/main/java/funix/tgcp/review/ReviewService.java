package funix.tgcp.review;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import funix.tgcp.user.User;
import funix.tgcp.user.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    
    
    @Autowired
    private  UserRepository userRepository;
    
    public List<Review> findTop3ByReviewedUserIdOrderByReviewDateDesc(Long reviewedUserId){
    	return reviewRepository.findTop3ByReviewedUserIdOrderByReviewDateDesc(reviewedUserId);
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
    
    @Transactional
    public void addReview(Long bookingId, Long reviewerId, Long reviewedUserId, int rating, String feedback) {
        if (hasUserReviewed(bookingId, reviewerId, reviewedUserId)) {
        	System.out.println("Bạn đã đánh giá người này rồi");
            throw new IllegalStateException("Bạn đã đánh giá người này rồi.");            
        }

        
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new IllegalArgumentException("Người đánh giá không hợp lệ."));
        User reviewedUser = userRepository.findById(reviewedUserId)
                .orElseThrow(() -> new IllegalArgumentException("Người được đánh giá không hợp lệ."));

        Review review = new Review();
        review.setReviewer(reviewer);
        review.setReviewedUser(reviewedUser);
        review.setRating(rating);
        review.setFeedback(feedback);

        reviewRepository.save(review);
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
}
