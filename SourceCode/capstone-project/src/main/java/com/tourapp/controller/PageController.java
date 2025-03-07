package com.tourapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tourapp.dto.UserDTO;
import com.tourapp.service.UserService;
import com.tourapp.util.ViewPaths;

@Controller
public class PageController {

	@Autowired
	UserService userService;

	@GetMapping("/")
    public String home() {
        return ViewPaths.HOME;
    }

    @GetMapping("/login")
    public String login() {
        return ViewPaths.LOGIN;
    }

    @GetMapping("/signup")
    public String signup() {
        return ViewPaths.SIGNUP;
    }

    @GetMapping("/tour-guides")
    public String tourGuides(Model model, @RequestParam(defaultValue = "all") String city) {
        model.addAttribute("city", city);
        return ViewPaths.TOUR_GUIDES;
    }

    @GetMapping("/become-a-guide")
    public String becomeAGuide() {
        return ViewPaths.BECOME_A_GUIDE;
    }

	@GetMapping("/profile")
	public String profile(Model model, Authentication authentication) {
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			UserDTO userDTO = userService.getUserProfile(userDetails.getUsername()); // Gọi service để lấy DTO
			model.addAttribute("user", userDTO);
		}
		return ViewPaths.PROFILE;
	}
}
