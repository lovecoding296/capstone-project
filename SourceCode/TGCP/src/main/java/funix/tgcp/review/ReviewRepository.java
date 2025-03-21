package funix.tgcp.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewedUserId(Long reviewedUserId); // Lấy các đánh giá của một người dùng
    List<Review> findByReviewerId(Long reviewerId); // Lấy các đánh giá do một người dùng thực hiện
    List<Review> findByTourId(Long tourId);
    List<Review> findByTourIdAndReviewerId(Long tourId, Long reviewer);
	boolean existsByTourIdAndReviewerIdAndReviewedUserId(Long tourId, Long reviewerId, Long reviewedUserId);
	Optional<Review> getReviewById(Long reviewId);
	void deleteByTourId(Long id);
	
	List<Review> findTop3ByReviewedUserIdOrderByReviewDateDesc(Long reviewedUserId);
}
