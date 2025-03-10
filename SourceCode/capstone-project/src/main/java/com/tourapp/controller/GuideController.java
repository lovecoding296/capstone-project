package com.tourapp.controller;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tourapp.entity.AppUser;
import com.tourapp.service.GuideService;
import com.tourapp.service.UserService;

@Controller
@RequestMapping("/guides")
public class GuideController {
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private GuideService guideService;
	
	@GetMapping
	public String listGuides(Model model) {
		List<AppUser> guides = guideService.findAll();
		Collections.reverse(guides);
		model.addAttribute("guides", guides);
		return "guide/guide-list";
	}
	
	
	@GetMapping("/{id}")
	public String showGuideProfile(@PathVariable Long id, Model model) {
		try {
			AppUser guide = guideService.findById(id);
			model.addAttribute("guide", guide);
			return "guide/guide-details";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			model.addAttribute("errorMessage", "Không có dữ liệu nào được tìm thấy!");
			return "error";
		}
	}
}
