package funix.tca.repository;

import funix.tca.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewedUserId(Long reviewedUserId); // Lấy các đánh giá của một người dùng
    List<Review> findByReviewerId(Long reviewerId); // Lấy các đánh giá do một người dùng thực hiện
    List<Review> findByTripId(Long tripId);
}
