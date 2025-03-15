package funix.tca.review;

import funix.tca.appuser.AppUser;
import funix.tca.appuser.AppUserService;
import funix.tca.trip.Trip;
import funix.tca.trip.TripService;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
	
	@Autowired
    private ReviewService reviewService;
	
	@Autowired
    private  TripService tripService;
	
	@Autowired
    private  AppUserService userService;


	@GetMapping("/{appUserId}")
    public String getReviewsById(@PathVariable Long appUserId, Model model, HttpSession session) {
        List<Review> reviews = reviewService.findByReviewedUserId(appUserId);
        
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
                
        model.addAttribute("reviews", reviews);
        model.addAttribute("review", new Review());
        model.addAttribute("loggedInUser", loggedInUser);

        return "review/reviewed-user-list";
    }
	
	@GetMapping("/reviewed/{appUserId}")
    public String getGivenReviewsById(@PathVariable Long appUserId, Model model, HttpSession session) {
        List<Review> reviews = reviewService.findByReviewerId(appUserId);
        
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
                
        model.addAttribute("reviews", reviews);
        model.addAttribute("review", new Review());
        model.addAttribute("loggedInUser", loggedInUser);

        return "review/given-review-list";
    }
    

    /**
     * Hiển thị danh sách đánh giá của chuyến đi
     */
    @GetMapping("/trip/{tripId}")
    public String listReviews(@PathVariable Long tripId, Model model, HttpSession session) {
        List<Review> reviews = reviewService.findByTripId(tripId);
        Trip trip = tripService.getTripById(tripId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chuyến đi."));
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        
        Set<AppUser> participants = trip.getParticipants();        
        participants.add(trip.getCreator());
        participants.remove(loggedInUser);
        
        model.addAttribute("reviews", reviews);
        model.addAttribute("trip", trip);
        model.addAttribute("participants", participants);
        model.addAttribute("review", new Review());
        model.addAttribute("loggedInUser", loggedInUser);

        return "review/review-list";
    }
    

    @GetMapping("/trip/{tripId}/new")
    public String createReview(@PathVariable Long tripId, Model model, HttpSession session) {
        List<Review> reviews = reviewService.findByTripId(tripId);
        Trip trip = tripService.getTripById(tripId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chuyến đi."));
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        
        Set<AppUser> participants = trip.getParticipants();        
        participants.add(trip.getCreator());
        participants.remove(loggedInUser);
        
        model.addAttribute("reviews", reviews);
        model.addAttribute("trip", trip);
        model.addAttribute("participants", participants);
        model.addAttribute("review", new Review());
        model.addAttribute("loggedInUser", loggedInUser);

        return "review/review-form";
    }

    /**
     * Xử lý thêm đánh giá mới
     */
    @PostMapping("/trip/{tripId}/new")
    public String addReview(@PathVariable Long tripId, @ModelAttribute Review review, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");

        if (reviewService.hasUserReviewed(tripId, loggedInUser.getId(), review.getReviewedUser().getId())) {
        	System.out.println("bạn đã đánh giá người này rồi");
        	
            return "redirect:/reviews/trip/{tripId}/new";
        }

        reviewService.addReview(tripId, loggedInUser.getId(), review.getReviewedUser().getId(),
                review.getRating(), review.getFeedback());
        
        return "redirect:/reviews/trip/" + tripId;
    }

    /**
     * Hiển thị form chỉnh sửa đánh giá
     */
    @GetMapping("/{reviewId}/edit")
    public String editReviewForm(@PathVariable Long reviewId, Model model, HttpSession session) {
        Optional<Review> reviewOpt = reviewService.getReviewById(reviewId);
        if (reviewOpt.isEmpty()) {
            return "redirect:/reviews/trip?error=Không tìm thấy đánh giá!";
        }

        Review review = reviewOpt.get();
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");

        if (!review.getReviewer().getId().equals(loggedInUser.getId())) {
            return "redirect:/reviews/trip/" + review.getTrip().getId() + "?error=Bạn không thể chỉnh sửa đánh giá của người khác!";
        }

        model.addAttribute("review", review);
        return "review/review-edit";
    }

    /**
     * Xử lý cập nhật đánh giá
     */
    @PostMapping("/{reviewId}/edit")
    public String updateReview(@PathVariable Long reviewId, @ModelAttribute Review review, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        Optional<Review> existingReview = reviewService.getReviewById(reviewId);

        if (existingReview.isEmpty() || !existingReview.get().getReviewer().getId().equals(loggedInUser.getId())) {
            return "redirect:/reviews/trip/" + existingReview.get().getTrip().getId() + "?error=Không thể chỉnh sửa!";
        }

        reviewService.updateReview(reviewId, review.getRating(), review.getFeedback());
        return "redirect:/reviews/trip/" + existingReview.get().getTrip().getId();
    }

    /**
     * Xử lý xóa đánh giá
     */
    @PostMapping("/{reviewId}/delete")
    public String deleteReview(@PathVariable Long reviewId, HttpSession session) {
        Optional<Review> reviewOpt = reviewService.getReviewById(reviewId);
        if (reviewOpt.isEmpty()) {
            return "redirect:/reviews/trip?error=Không tìm thấy đánh giá!";
        }

        Review review = reviewOpt.get();
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");

        if (!review.getReviewer().getId().equals(loggedInUser.getId())) {
            return "redirect:/reviews/trip/" + review.getTrip().getId() + "?error=Bạn không thể xóa đánh giá của người khác!";
        }

        reviewService.deleteById(reviewId);
        return "redirect:/reviews/trip/" + review.getTrip().getId();
    }
}
