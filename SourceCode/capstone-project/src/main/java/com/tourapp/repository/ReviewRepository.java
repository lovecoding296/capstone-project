package com.tourapp.repository;


import com.tourapp.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTourId(Long tourId);

	List<Review> findTop3ByOrderByRating();
    
    


}

