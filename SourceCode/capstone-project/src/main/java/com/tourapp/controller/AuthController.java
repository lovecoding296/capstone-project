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
import com.tourapp.service.UserService;
import com.tourapp.util.ViewPaths;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService accountService;
    
	@GetMapping("/")
    public String home() {
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
        return ViewPaths.BECOME_A_GUIDE;
    }

    @PostMapping("/signup/tourist")
    public String registerTourist(@Valid @ModelAttribute() AppUser user, BindingResult result, Model model, RedirectAttributes  redirectAttributes) {
        if (result.hasErrors()) {
        	model.addAttribute("guideForm", user);
            return "signup";
        }
        accountService.registerTourist(user);
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
        accountService.registerTourGuide(user);
        logger.info("New tour guide registered: {}", user.getEmail());
        redirectAttributes.addFlashAttribute("successMessage", "Bạn đã tạo tài khoản thành công!");
        return "redirect:/login";
    }
}
