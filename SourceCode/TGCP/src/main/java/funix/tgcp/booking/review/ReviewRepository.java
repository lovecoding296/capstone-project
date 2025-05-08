package funix.tgcp.booking.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import funix.tgcp.user.User;

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
	Optional<Review> findByBookingIdAndReviewedUserId(Long bookingId, Long reviewedUserId);
	List<Review> findByReviewedUserIdOrderByReviewDateDesc(Long reviewedUserId);
	List<Review> findByReviewedUserIdOrderByRatingDesc(Long reviewedUserId);
	
	
	// Đếm số review của một user
    long countByReviewedUser(User reviewedUser);
	
    // Tính điểm trung bình của user
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewedUser = :user")
    Double findAverageRatingByReviewedUser(@Param("user") User user);
    
    
    boolean existsByReviewerIdAndBookingId(Long reviewerId, Long bookingId);    
	
}
