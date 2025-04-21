package funix.tgcp.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewedUserId(Long reviewedUserId); // Lấy các đánh giá của một người dùng
    List<Review> findByReviewerId(Long reviewerId); // Lấy các đánh giá do một người dùng thực hiện
    List<Review> findByBookingId(Long bookingId);
    List<Review> findByBookingIdAndReviewerId(Long bookingId, Long reviewer);
	boolean existsByBookingIdAndReviewerIdAndReviewedUserId(Long bookingId, Long reviewerId, Long reviewedUserId);
	Optional<Review> getReviewById(Long reviewId);
	void deleteByBookingId(Long id);
	
	List<Review> findTop3ByReviewedUserIdOrderByReviewDateDesc(Long reviewedUserId);
}
