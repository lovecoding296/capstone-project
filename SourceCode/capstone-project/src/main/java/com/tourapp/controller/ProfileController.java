package com.tourapp.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.tourapp.entity.AppUser;
import com.tourapp.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserService userService;

	/*
	 * @GetMapping public ResponseEntity<?> getUserProfile(Authentication
	 * authentication) { if (authentication == null ||
	 * !(authentication.getPrincipal() instanceof UserDetails)) { return
	 * ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated"
	 * ); }
	 * 
	 * UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	 * AppUser user = userService.findByEmail(userDetails.getUsername());
	 * 
	 * if (user == null) { return
	 * ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"); }
	 * 
	 * return ResponseEntity.ok(user); }
	 */

	@PostMapping("/update")
	public ResponseEntity<?> updateProfile(@RequestParam("name") String name, @RequestParam("city") String city,
			@RequestParam("phone") String phone, @RequestParam("profilePicture") MultipartFile file) {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String email = auth.getName();
			userService.updateUserProfile(email, name, city, phone, file);
			return ResponseEntity.ok("User updated successfully");
		} catch (IllegalArgumentException e) {
			logger.error("Phi: errorrrrrrrrrrr {}", e.getMessage());		
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
		}
	}

}
