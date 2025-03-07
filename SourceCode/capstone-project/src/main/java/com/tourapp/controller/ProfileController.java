package com.tourapp.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tourapp.entity.AppUser;
import com.tourapp.service.UserService;

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
    public String updateProfile(@RequestParam("name") String name,
                                @RequestParam("city") String city,
                                @RequestParam("phone") String phone,
                                @RequestParam("profilePicture") MultipartFile file) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        logger.info("name {} city {} phone {}", name, city, phone);

        userService.updateUserProfile(email, name, city, phone, file);
        return "redirect:/profile?success";
    }
}

