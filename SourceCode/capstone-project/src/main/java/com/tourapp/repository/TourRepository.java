package com.tourapp.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tourapp.entity.Tour;

public interface TourRepository extends JpaRepository<Tour, Long> {
	
	@Query("SELECT t FROM Tour t JOIN t.guide g JOIN g.languages l WHERE l IN :languages")
    List<Tour> findByGuideLanguages(@Param("languages") Set<String> languages);
}
