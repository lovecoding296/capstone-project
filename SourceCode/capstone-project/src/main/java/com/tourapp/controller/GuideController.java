package com.tourapp.controller;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tourapp.entity.AppUser;
import com.tourapp.exception.EmailAlreadyExistsException;
import com.tourapp.service.GuideService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/guides")
public class GuideController {
	private static final Logger logger = LoggerFactory.getLogger(GuideController.class);

	@Autowired
	private GuideService guideService;

	/**
	 * Hiển thị form đăng ký làm hướng dẫn viên (chỉ dành cho user chưa đăng nhập)
	 */
	@GetMapping("/apply")
	public String applyToBeGuide(HttpSession session, Model model) {
		if (session.getAttribute("loggedInUser") != null) {
			logger.warn("Người dùng đã đăng nhập, chuyển hướng đến trang home.");
			return "redirect:/home"; // Nếu đã login, không cho phép apply
		}
		model.addAttribute("user", new AppUser());
		return "guides/guide-apply";
	}

	/**
	 * Xử lý đăng ký làm hướng dẫn viên
	 */
	@PostMapping("/apply")
	public String registerTourGuide(@Valid @ModelAttribute("user") AppUser user, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("user", user);
			redirectAttributes.addFlashAttribute("error", "Có lỗi trong quá trình đăng ký.");
			return "redirect:/guides/apply";
		}

		try {
			guideService.registerGuide(user);
			logger.info("New tour guide registered: {}", user.getEmail());
			redirectAttributes.addFlashAttribute("successMessage", "Bạn đã tạo tài khoản thành công!");
			return "redirect:/login";
		} catch (EmailAlreadyExistsException e) {
			redirectAttributes.addFlashAttribute("error", "Email đã tồn tại!");
			redirectAttributes.addFlashAttribute("user", user);
			return "redirect:/guides/apply";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Xảy ra lỗi khi đăng ký, thử lại sau!");
			redirectAttributes.addFlashAttribute("user", user);
			return "redirect:/guides/apply";
		}
	}

	/**
	 * Hiển thị form nâng cấp tài khoản Tourist lên Guide
	 */
	@GetMapping("/upgrade")
	public String upgradeToGuide(HttpSession session, Model model) {
		AppUser user = (AppUser) session.getAttribute("loggedInUser");

		if (user == null) {
			logger.warn("Người dùng chưa đăng nhập, chuyển hướng đến trang đăng nhập.");
			return "redirect:/login"; // Điều hướng đến trang đăng nhập nếu chưa đăng nhập
		}

		model.addAttribute("guideForm", user);
		return "guides/guide-upgrade";
	}
}
