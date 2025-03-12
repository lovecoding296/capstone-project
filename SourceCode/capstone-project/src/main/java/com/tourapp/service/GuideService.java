package com.tourapp.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tourapp.entity.AppUser;
import com.tourapp.entity.Role;
import com.tourapp.exception.EmailAlreadyExistsException;
import com.tourapp.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GuideService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	
    public List<AppUser> findAll() {
        return userRepository.findByRole(Role.ROLE_GUIDE);
    }
    
    public AppUser findById(Long id) {
        return userRepository.findById(id)
                             .orElseThrow(() -> new EntityNotFoundException("Tour not found with ID: " + id));
    }

	public AppUser registerGuide(AppUser user) throws EmailAlreadyExistsException {
		logger.info("registerGuide");
		return userService.registerUser(user, Role.ROLE_GUIDE);
	}
}
