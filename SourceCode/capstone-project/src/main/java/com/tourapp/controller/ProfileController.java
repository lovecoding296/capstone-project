package com.tourapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tourapp.entity.AppUser;
import com.tourapp.service.UserService;
import com.tourapp.util.ViewPaths;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/profile")
public class ProfileController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserService userService;
	
	@GetMapping()
	public String profile(Model model, Authentication authentication) {
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			AppUser user = userService.findByEmail(userDetails.getUsername());
			model.addAttribute("user", user);
		}
		return ViewPaths.PROFILE;
	}

	@PostMapping()
	public String updateProfile(@Valid @ModelAttribute("user") AppUser user, 
			BindingResult result,
			@RequestParam("profilePictureFile") MultipartFile file, Model model) {
		
		if (result.hasErrors()) {
			logger.error(result.getAllErrors().toString());
			model.addAttribute("user", user);
			model.addAttribute("errorMessage", "Lỗi, Cập nhật không thành công!");
			return "profile";
        }
		try {
			AppUser updatedUser = userService.updateCurrentUser(user, file);
			model.addAttribute("user", updatedUser);
			model.addAttribute("successMessage", "Cập nhật thành công!");
		    return "profile";
		} catch (Exception e) {
			model.addAttribute("user", user);
			model.addAttribute("errorMessage", "Lỗi, Cập nhật không thành công!");
			return "profile";
		}
	}
}

