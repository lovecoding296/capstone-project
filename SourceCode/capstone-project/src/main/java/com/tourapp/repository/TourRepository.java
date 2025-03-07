package com.tourapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tourapp.entity.Tour;

public interface TourRepository extends JpaRepository<Tour, Long> {
}
