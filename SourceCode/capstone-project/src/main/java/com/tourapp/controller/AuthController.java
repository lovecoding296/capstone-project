package com.tourapp.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tourapp.entity.AppUser;
import com.tourapp.entity.Review;
import com.tourapp.entity.Tour;
import com.tourapp.service.ReviewService;
import com.tourapp.service.TourService;
import com.tourapp.service.UserService;
import jakarta.validation.Valid;

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
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new AppUser());
        return "signup"; // Trả về trang signup.html
    }
    

    @GetMapping("/become-a-guide")
    public String becomeAGuide(Model model) {
    	model.addAttribute("guideForm", new AppUser());
        return "become-a-guide";
    }

    @PostMapping("/signup/tourist")
    public String registerTourist(@Valid @ModelAttribute() AppUser user, BindingResult result, Model model, RedirectAttributes  redirectAttributes) {
        if (result.hasErrors()) {
        	model.addAttribute("guideForm", user);
            return "signup";
        }
        userService.registerTourist(user);
        logger.info("New tourist registered: {}", user.getEmail());
        redirectAttributes.addFlashAttribute("successMessage", "Bạn đã tạo tài khoản thành công!");
        return "redirect:/login"; // Chuyển đến trang đăng nhập sau khi đăng ký thành công
    }

    @PostMapping("/signup/tour-guide")
    public String registerTourGuide(@Valid @ModelAttribute() AppUser user, BindingResult result, Model model, RedirectAttributes  redirectAttributes) {
        if (result.hasErrors()) {
        	model.addAttribute("guideForm", user);
            return "signup";
        }
        userService.registerTourGuide(user);
        logger.info("New tour guide registered: {}", user.getEmail());
        redirectAttributes.addFlashAttribute("successMessage", "Bạn đã tạo tài khoản thành công!");
        return "redirect:/login";
    }
}
