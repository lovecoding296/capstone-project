package funix.tca.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import funix.tca.entity.Review;
import funix.tca.service.AppUserService;
import funix.tca.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
	
	private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);


    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AppUserService appUserService;

    // Lấy tất cả các đánh giá
    @GetMapping("/")
    public String listReviews(Model model) {
    	logger.info("listReviews");
        List<Review> reviews = reviewService.findAll();
        model.addAttribute("reviews", reviews);
        
        logger.info("return \"review/list\";");
        return "review/list"; // Trang danh sách các đánh giá
    }

    // Lấy các đánh giá của một người dùng
    @GetMapping("/user/{userId}")
    public String listReviewsByUser(@PathVariable Long userId, Model model) {
        List<Review> reviews = reviewService.findByReviewedUserId(userId);
        model.addAttribute("reviews", reviews);
        return "review/list"; // Trang các đánh giá của người dùng
    }

    // Tạo mới một đánh giá
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Review review = new Review();
        model.addAttribute("review", review);
        model.addAttribute("users", appUserService.findAll()); // Cung cấp danh sách người dùng
        return "review/form"; // Trang tạo mới đánh giá
    }

    @PostMapping("/new")
    public String createReview(@Valid @ModelAttribute Review review, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", appUserService.findAll());
            return "review/form"; // Trả lại trang nếu có lỗi
        }

        // Lưu đánh giá
        reviewService.save(review);
        return "redirect:/reviews/"; // Chuyển hướng về trang danh sách
    }

    // Xóa một đánh giá
    @GetMapping("/{id}/delete")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteById(id);
        return "redirect:/reviews/"; // Chuyển hướng về trang danh sách
    }
}
