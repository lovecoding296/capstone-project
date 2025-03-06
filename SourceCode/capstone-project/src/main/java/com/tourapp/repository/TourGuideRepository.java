package com.tourapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tourapp.entity.TourGuide;

@Repository
public interface TourGuideRepository extends JpaRepository<TourGuide, Long> {
	
    // Kiểm tra xem email đã tồn tại chưa
    boolean existsByEmail(String email);
    
    // Tìm kiếm Tourist theo email
    TourGuide findByEmail(String email);
}
