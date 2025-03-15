package funix.tca.home.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import funix.tca.appuser.AppUser;
import funix.tca.appuser.AppUserService;
import funix.tca.exception.EmailVerificationException;
import funix.tca.post.Post;
import funix.tca.post.PostCategory;
import funix.tca.post.PostService;
import funix.tca.review.ReviewController;
import funix.tca.trip.Trip;
import funix.tca.trip.TripCategory;
import funix.tca.trip.TripService;
import funix.tca.trip.request.TripRequestService;
import funix.tca.util.FileUploadHelper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

	@Autowired
	private AppUserService appUserService;

	@Autowired
	private TripService tripService;
	
	@Autowired
	private TripRequestService tripRequestService;

	@Autowired
	private PostService postService;

	@GetMapping("/")
	public String home(Model model, HttpSession session) {
		logger.info("home");
		List<Trip> trips = tripService.getLatestTrips();
		List<Post> posts = postService.getLatestPosts();

		model.addAttribute("postCategories", PostCategory.values());
		model.addAttribute("tripCategories", TripCategory.values());
		model.addAttribute("trips", trips);
		model.addAttribute("posts", posts);

		AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
		if (loggedInUser != null) {
			// Tạo một Map để lưu trạng thái tham gia của người dùng với từng chuyến đi
			Map<Long, Boolean> participantStatus = new HashMap<>();
			Map<Long, Boolean> requestStatus = new HashMap<>();

			for (Trip trip : trips) {
				
				for(AppUser user : trip.getParticipants()) {
					System.out.println("user id " + user.getId() + " loggedInUser is " + loggedInUser.getId() + " isParticipant " + trip.getParticipants().contains(loggedInUser) + " name " +user.getFullName() );
				}
				
				boolean isParticipant = trip.getParticipants().contains(loggedInUser);
				participantStatus.put(trip.getId(), isParticipant);
				
				boolean hasRequested = !isParticipant && tripRequestService.hasUserRequested(loggedInUser, trip);			
	            requestStatus.put(trip.getId(), hasRequested);
			}

			model.addAttribute("participantStatus", participantStatus);
			model.addAttribute("requestStatus", requestStatus);
		}

		return "home";
	}

	@GetMapping("/signup")
	public String showSignupForm(HttpSession session, Model model) {
		if (session.getAttribute("loggedInUser") != null) {
			return "redirect:/"; // Nếu đã login, chuyển sang home
		}
		model.addAttribute("appUser", new AppUser());
		return "signup"; // Trả về trang signup.html
	}
	
	@PostMapping("/signup")
    public String createAppUser(@Valid @ModelAttribute AppUser appUser, BindingResult result, @RequestParam("cccdFile") MultipartFile cccdFile,  Model model) {
        if (result.hasErrors()) {
        	model.addAttribute("appUser", appUser);
            return "signup";
        }
		try {
			String cccd = FileUploadHelper.uploadFile(cccdFile);			
			if(cccd != null) {
	    		appUser.setCccd(cccd);
	    	}
			appUserService.save(appUser);
			System.out.println("Đăng ký thành công, hãy chờ quản trị viên phê duyệt tài khoản.");
			model.addAttribute("successMessage", "Đăng ký thành công, hãy chờ quản trị viên phê duyệt tài khoản.");
			model.addAttribute("appUser", new AppUser());
			return "signup";
		} catch (IOException e) {
			
			e.printStackTrace();
		}        
		model.addAttribute("appUser", appUser);
		model.addAttribute("errorMessage", "Có lỗi khi đăng ký hãy thử lại sau.");
        return "signup";
    }

	@GetMapping("/login")
	public String login(HttpSession session) {
		if (session.getAttribute("loggedInUser") != null) {
			return "redirect:/"; // Nếu đã login, chuyển sang home
		}
		return "login";
	}

	@GetMapping("/verify")
	public String verifyEmail(@RequestParam("token") String token, Model model) {
		try {
			appUserService.verifyEmail(token);
			model.addAttribute("successMessage", "Email verified successfully! You can now log in.");
		} catch (EmailVerificationException e) {
			e.printStackTrace();
			model.addAttribute("error", "Invalid verification token or the token has expired.");
		}
		return "login";
	}
}
