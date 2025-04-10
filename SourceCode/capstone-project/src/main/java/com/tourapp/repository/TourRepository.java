package com.tourapp.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tourapp.entity.AppUser;
import com.tourapp.entity.Tour;

public interface TourRepository extends JpaRepository<Tour, Long> {

	@Query("SELECT t FROM Tour t JOIN t.guide g JOIN g.languages l WHERE l IN :languages")
	List<Tour> findByGuideLanguages(@Param("languages") Set<String> languages);

	// Tìm 4 tour có averageRating cao nhất
    List<Tour> findTop4ByOrderByAverageRatingDesc();

	List<Tour> findAll();

	List<Tour> findByGuide(AppUser guide);

	Optional<Tour> findById(Long id);
	
	
}
