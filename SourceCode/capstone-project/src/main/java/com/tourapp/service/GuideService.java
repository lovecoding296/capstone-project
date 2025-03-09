package com.tourapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tourapp.entity.AppUser;
import com.tourapp.entity.Role;
import com.tourapp.entity.Tour;
import com.tourapp.repository.GuideRepository;
import com.tourapp.repository.TourRepository;
import com.tourapp.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GuideService {
	
	@Autowired
    private UserRepository userRepository;
	
	
    public List<AppUser> findAll() {
        return userRepository.findByRole(Role.ROLE_TOUR_GUIDE);
    }
    
    public AppUser findById(Long id) {
        return userRepository.findById(id)
                             .orElseThrow(() -> new EntityNotFoundException("Tour not found with ID: " + id));
    }
}
