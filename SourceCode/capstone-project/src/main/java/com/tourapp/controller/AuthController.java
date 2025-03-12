package com.tourapp.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.tourapp.entity.AppUser;
import com.tourapp.entity.Review;
import com.tourapp.entity.Tour;
import com.tourapp.exception.EmailVerificationException;
import com.tourapp.service.ReviewService;
import com.tourapp.service.TourService;
import com.tourapp.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private TourService tourService;

	@Autowired
	private ReviewService reviewService;

	@GetMapping("/")
	public String home(Model model) {
		List<Tour> tours = tourService.findTop4ByOrderByAverageRatingDesc();
		List<AppUser> users = userService.findTop4ByOrderByAverageRatingDesc();
		List<Review> reviews = reviewService.findTop3ByOrderByRating();
		model.addAttribute("tours", tours);
		model.addAttribute("guides", users);
		model.addAttribute("reviews", reviews);
		return "home";
	}

	@GetMapping("/login")
	public String login(HttpSession session) {
		if (session.getAttribute("loggedInUser") != null) {
            return "redirect:/"; // Nếu đã login, chuyển sang home
        }
		return "login";
	}


	@GetMapping("/verify")
	public String verifyEmail(@RequestParam("token") String token, Model model) {
		try {
			userService.verifyEmail(token);
			model.addAttribute("successMessage", "Email verified successfully! You can now log in.");
		} catch (EmailVerificationException e) {
			e.printStackTrace();
			model.addAttribute("error", "Invalid verification token or the token has expired.");
		}
		return "login";
	}

	
	
}
