package com.tourapp.service;

import com.tourapp.entity.Tourist;
import com.tourapp.repository.TouristRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TouristService {
	
	@Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TouristRepository touristRepository;

    public Tourist registerTourist(Tourist tourist) {
        if (touristRepository.existsByEmail(tourist.getEmail())) {
            throw new IllegalArgumentException("Email already exists");       }

        tourist.setPassword(passwordEncoder.encode(tourist.getPassword()));
        return touristRepository.save(tourist);
    }
}

