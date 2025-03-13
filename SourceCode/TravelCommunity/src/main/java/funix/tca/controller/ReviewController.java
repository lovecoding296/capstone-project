package funix.tca.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import funix.tca.entity.AppUser;
import funix.tca.entity.Review;
import funix.tca.entity.Trip;
import funix.tca.service.AppUserService;
import funix.tca.service.ReviewService;
import funix.tca.service.TripService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AppUserService appUserService;
    
    @Autowired
    private TripService tripService;

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
    @GetMapping("/trip/{id}/new")
    public String showCreateForm(Model model, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
        }

        model.addAttribute("review", new Review());
        model.addAttribute("users", appUserService.findAll());
        return "review/form"; // Trang tạo mới đánh giá
    }

    @PostMapping("/trip/{tripId}/new")
    public String createReview(@PathVariable Long tripId,@Valid @ModelAttribute Review review, BindingResult result, Model model, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
        }

        if (result.hasErrors()) {
            model.addAttribute("users", appUserService.findAll());
            return "review/form"; // Trả lại trang nếu có lỗi
        }
        Optional<Trip> tripOp = tripService.findById(tripId);
        if(tripOp.isPresent()) {
        	review.setTrip(tripOp.get());
            review.setReviewer(loggedInUser); // Gán người đánh giá là loggedInUser
            reviewService.save(review);
        } else {
        	model.addAttribute("users", appUserService.findAll());
        }            
        return "redirect:/trips/{id}/details";
    }

    // Xóa một đánh giá
    @GetMapping("/{id}/delete")
    public String deleteReview(@PathVariable Long id, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
        }

        Review review = reviewService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid review ID: " + id));

        // Kiểm tra quyền xóa (chỉ người viết đánh giá hoặc admin mới có quyền)
        if (!review.getReviewer().getId().equals(loggedInUser.getId()) && !loggedInUser.isAdmin()) {
            return "redirect:/reviews/?error=unauthorized";
        }

        reviewService.deleteById(id);
        return "redirect:/reviews/";
    }
}
