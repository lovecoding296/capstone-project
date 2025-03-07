package com.tourapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tourapp.entity.Tour;
import com.tourapp.service.TourService;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
public class TourController {
	
	@Autowired
    private TourService tourService;


    @PostMapping
    public ResponseEntity<Tour> createTour(@RequestBody Tour tour) {
        Tour newTour = tourService.createTour(tour);
        return ResponseEntity.ok(newTour);
    }

    @GetMapping
    public ResponseEntity<List<Tour>> getAllTours() {
        List<Tour> tours = tourService.getAllTours();
        return ResponseEntity.ok(tours);
    }
}
