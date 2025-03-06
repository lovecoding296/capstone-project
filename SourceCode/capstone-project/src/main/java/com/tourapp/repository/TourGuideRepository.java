package com.tourapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tourapp.entity.TourGuide;

@Repository
public interface TourGuideRepository extends JpaRepository<TourGuide, Long> {
	
}
