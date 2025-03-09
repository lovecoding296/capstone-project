package com.tourapp.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tourapp.entity.AppUser;
import com.tourapp.entity.Tour;
import com.tourapp.repository.TourRepository;

import jakarta.persistence.EntityNotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TourService {
	
	@Autowired
	private UserService userService;
	
	@Autowired
    private TourRepository tourRepository;


    public Tour save(Tour tour, MultipartFile file) throws IOException {  	    	
		AppUser user = userService.getCurrentAuthenticatedUser();
		tour.setGuide(user);
    	uploadThumnail(tour, file);
        return tourRepository.save(tour);
    }

    public List<Tour> findAll() {
        return tourRepository.findAll();
    }
    
    public Tour findById(Long id) {
        return tourRepository.findById(id)
                             .orElseThrow(() -> new EntityNotFoundException("Tour not found with ID: " + id));
    }

    
    private void uploadThumnail(Tour tour, MultipartFile file) throws IOException {
    	Set<String> allowedMimeTypes = Set.of("image/jpeg", "image/png", "image/webp");
        String contentType = file.getContentType();
        if (contentType == null || !allowedMimeTypes.contains(contentType)) {
            throw new IllegalArgumentException("Invalid file type. Only JPEG, PNG, and WEBP are allowed.");
        }

        String fileName = tour.getId() + "_" + file.getOriginalFilename();
        String uploadDir = "uploads/tours/";

        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        File destination = new File(uploadDir + fileName);
        Files.copy(file.getInputStream(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

        tour.setThumbnail("/uploads/tours/" + fileName);
    }
    
}

