package funix.tgcp.review;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import funix.tgcp.user.User;
import funix.tgcp.user.UserService;
import funix.tgcp.tour.Tour;
import funix.tgcp.tour.TourService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
	
	@Autowired
    private ReviewService reviewService;
	
	@Autowired
    private  TourService tourService;
	
	@Autowired
    private  UserService userService;


	@GetMapping("/{userId}")
    public String getReviewsById(@PathVariable Long userId, Model model, HttpSession session) {
        List<Review> reviews = reviewService.findByReviewedUserId(userId);
        
        User loggedInUser = (User) session.getAttribute("loggedInUser");
                
        model.addAttribute("reviews", reviews);
        model.addAttribute("review", new Review());
        model.addAttribute("loggedInUser", loggedInUser);

        return "review/reviewed-user-list";
    }
	
	@GetMapping("/reviewed/{userId}")
    public String getGivenReviewsById(@PathVariable Long userId, Model model, HttpSession session) {
        List<Review> reviews = reviewService.findByReviewerId(userId);
        
        User loggedInUser = (User) session.getAttribute("loggedInUser");
                
        model.addAttribute("reviews", reviews);
        model.addAttribute("review", new Review());
        model.addAttribute("loggedInUser", loggedInUser);

        return "review/given-review-list";
    }
    

    /**
     * Hiển thị danh sách đánh giá của Tour
     */
    @GetMapping("/tour/{tourId}")
    public String listReviews(@PathVariable Long tourId, Model model, HttpSession session) {
        List<Review> reviews = reviewService.findByTourId(tourId);
        Tour tour = tourService.findById(tourId);
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        
        Set<User> participants = tour.getParticipants();        
        participants.add(tour.getCreator());
        participants.remove(loggedInUser);
        
        model.addAttribute("reviews", reviews);
        model.addAttribute("tour", tour);
        model.addAttribute("participants", participants);
        model.addAttribute("review", new Review());
        model.addAttribute("loggedInUser", loggedInUser);

        return "review/review-list";
    }
    

    @GetMapping("/tour/{tourId}/new")
    public String createReview(@PathVariable Long tourId, Model model, HttpSession session) {
        List<Review> reviews = reviewService.findByTourId(tourId);
        Tour tour = tourService.findById(tourId);
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        
        Set<User> participants = tour.getParticipants();        
        participants.add(tour.getCreator());
        participants.remove(loggedInUser);
        
        model.addAttribute("reviews", reviews);
        model.addAttribute("tour", tour);
        model.addAttribute("participants", participants);
        model.addAttribute("review", new Review());
        model.addAttribute("loggedInUser", loggedInUser);

        return "review/review-form";
    }

    /**
     * Xử lý thêm đánh giá mới
     */
    @PostMapping("/tour/{tourId}/new")
    public String addReview(@PathVariable Long tourId, @ModelAttribute Review review, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (reviewService.hasUserReviewed(tourId, loggedInUser.getId(), review.getReviewedUser().getId())) {
        	System.out.println("bạn đã đánh giá người này rồi");
        	
            return "redirect:/reviews/tour/{tourId}/new";
        }

        reviewService.addReview(tourId, loggedInUser.getId(), review.getReviewedUser().getId(),
                review.getRating(), review.getFeedback());
        
        return "redirect:/reviews/tour/" + tourId;
    }

    /**
     * Hiển thị form chỉnh sửa đánh giá
     */
    @GetMapping("/{reviewId}/edit")
    public String editReviewForm(@PathVariable Long reviewId, Model model, HttpSession session) {
        Optional<Review> reviewOpt = reviewService.getReviewById(reviewId);
        if (reviewOpt.isEmpty()) {
            return "redirect:/reviews/tour?error=Không tìm thấy đánh giá!";
        }

        Review review = reviewOpt.get();
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (!review.getReviewer().getId().equals(loggedInUser.getId())) {
            return "redirect:/reviews/tour/" + review.getTour().getId() + "?error=Bạn không thể chỉnh sửa đánh giá của người khác!";
        }

        model.addAttribute("review", review);
        return "review/review-edit";
    }

    /**
     * Xử lý cập nhật đánh giá
     */
    @PostMapping("/{reviewId}/edit")
    public String updateReview(@PathVariable Long reviewId, @ModelAttribute Review review, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        Optional<Review> existingReview = reviewService.getReviewById(reviewId);

        if (existingReview.isEmpty() || !existingReview.get().getReviewer().getId().equals(loggedInUser.getId())) {
            return "redirect:/reviews/tour/" + existingReview.get().getTour().getId() + "?error=Không thể chỉnh sửa!";
        }

        reviewService.updateReview(reviewId, review.getRating(), review.getFeedback());
        return "redirect:/reviews/tour/" + existingReview.get().getTour().getId();
    }

    /**
     * Xử lý xóa đánh giá
     */
    @PostMapping("/{reviewId}/delete")
    public String deleteReview(@PathVariable Long reviewId, HttpSession session) {
        Optional<Review> reviewOpt = reviewService.getReviewById(reviewId);
        if (reviewOpt.isEmpty()) {
            return "redirect:/reviews/tour?error=Không tìm thấy đánh giá!";
        }

        Review review = reviewOpt.get();
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (!review.getReviewer().getId().equals(loggedInUser.getId())) {
            return "redirect:/reviews/tour/" + review.getTour().getId() + "?error=Bạn không thể xóa đánh giá của người khác!";
        }

        reviewService.deleteById(reviewId);
        return "redirect:/reviews/tour/" + review.getTour().getId();
    }
}
