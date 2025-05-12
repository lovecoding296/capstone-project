package funix.tgcp.booking.review;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	
	List<Review> findByReviewedUserIdOrderByRatingDesc(Long reviewedUserId); // Lấy các đánh giá của một Guide
    boolean existsByReviewerIdAndBookingId(Long reviewerId, Long bookingId);    
	
}
