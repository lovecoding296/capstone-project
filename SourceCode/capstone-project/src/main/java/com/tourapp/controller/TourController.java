package com.tourapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.tourapp.entity.Tour;
import com.tourapp.service.TourService;
import com.tourapp.service.UserService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tours")
public class TourController {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	
	@Autowired
	private TourService tourService;

	@GetMapping
	public String listTours(Model model) {
		List<Tour> tours = tourService.findAll();
		Collections.reverse(tours);
		model.addAttribute("tours", tours);
		return "tour/tour-list";
	}
   
	@GetMapping("/create")
	public String showCreateForm(Model model) {
		model.addAttribute("tour", new Tour());
		return "tour/tour-form";
	}

	@PostMapping("/save")
	public String saveTour(@ModelAttribute Tour tour, @RequestParam("thumbnailFile") MultipartFile file,
			Model model) {
		
		logger.info("tour id:" + tour.getId() + ", email ");
		
		try {
			tourService.save(tour, file);
			model.addAttribute("successMessage", "Tạo tour mới thành công!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("errorMessage", "Lỗi khi tạo tour!");
		}
		return "redirect:/tours";
	}
	
	
	@GetMapping("/{id}")
	public String showTour(@PathVariable Long id, Model model) {
		try {
			Tour tour = tourService.findById(id);
			model.addAttribute("tour", tour);
			return "tour/tour-details";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			model.addAttribute("errorMessage", "Không có dữ liệu nào được tìm thấy!");
			return "error";
		}
	}

}
