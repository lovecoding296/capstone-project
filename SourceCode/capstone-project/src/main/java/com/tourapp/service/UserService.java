package com.tourapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tourapp.dto.UserDTO;
import com.tourapp.entity.AppUser;
import com.tourapp.entity.Role;
import com.tourapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	public AppUser registerTourist(AppUser user) {
		return registerUser(user, Role.ROLE_TOURIST);
	}

	public AppUser registerTourGuide(AppUser user) {
		return registerUser(user, Role.ROLE_TOUR_GUIDE);
	}

	private AppUser registerUser(AppUser user, Role role) {
		if (userRepository.existsByEmail(user.getEmail())) {
			logger.warn("Attempt to register with existing email: {}", user.getEmail());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(role);

		AppUser savedUser = userRepository.save(user);
		logger.info("New user registered: {} with role {}", savedUser.getEmail(), savedUser.getRole());

		return savedUser;
	}

	public AppUser findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public UserDTO getUserProfile(String email) {
		AppUser user = userRepository.findByEmail(email);
		if (user != null) {
			return new UserDTO(user);
		}
		return null;
	}

}
