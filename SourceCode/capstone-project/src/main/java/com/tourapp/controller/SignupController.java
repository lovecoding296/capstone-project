package com.tourapp.controller;

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
import com.tourapp.entity.Role;
import com.tourapp.exception.EmailAlreadyExistsException;
import com.tourapp.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class SignupController {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/signup")
	public String showSignupForm(HttpSession session, Model model) {
		if (session.getAttribute("loggedInUser") != null) {
            return "redirect:/"; // Nếu đã login, chuyển sang home
        }
		model.addAttribute("user", new AppUser());
		return "signup"; // Trả về trang signup.html
	}

	@PostMapping("/signup")
	public String registerTourist(@Valid @ModelAttribute() AppUser user, BindingResult result, Model model,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addAttribute("user", user);
			return "redirect:/signup";
		}
		try {
			logger.info("New tourist registering");
			userService.registerUser(user, Role.ROLE_TOURIST);
			logger.info("New tourist registered: {}", user.getEmail());
			redirectAttributes.addFlashAttribute("successMessage",
					"Bạn đã tạo tài khoản thành công, Hãy xác thực email trước khi đăng nhập!");
			return "redirect:/login"; // Chuyển đến trang đăng nhập sau khi đăng ký thành công
		} catch (EmailAlreadyExistsException e) {
			redirectAttributes.addAttribute("error", "Email đã tồn tại!");
			redirectAttributes.addAttribute("user", user);
			return "redirect:/signup";
		} catch (Exception e) {
			redirectAttributes.addAttribute("error", "Xảy ra lỗi khi đăng ký, thử lại sau!");
			redirectAttributes.addAttribute("user", user);
			return "redirect:/signup";
		}
	}
}
