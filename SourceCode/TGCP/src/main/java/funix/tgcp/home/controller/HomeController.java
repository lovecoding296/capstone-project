package funix.tgcp.home.controller;

import java.io.IOException;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.user.User;
import funix.tgcp.user.UserService;
import funix.tgcp.exception.EmailAlreadyExistsException;
import funix.tgcp.exception.EmailVerificationException;
import funix.tgcp.post.Post;
import funix.tgcp.post.PostCategory;
import funix.tgcp.post.PostService;
import funix.tgcp.review.ReviewController;
import funix.tgcp.trip.Trip;
import funix.tgcp.trip.TripCategory;
import funix.tgcp.trip.TripService;
import funix.tgcp.util.FileUploadHelper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private TripService tripService;
	

	@Autowired
	private PostService postService;
	
	@Autowired
	private FileUploadHelper fileUploadHelper;

	@GetMapping("/")
	public String home(Model model, HttpSession session) {
		logger.info("home");
		List<Trip> trips = tripService.findAll();
		List<Post> posts = postService.getLatestPosts();

		model.addAttribute("postCategories", PostCategory.values());
		model.addAttribute("tripCategories", TripCategory.values());
		model.addAttribute("trips", trips);
		model.addAttribute("posts", posts);

		User loggedInUser = (User) session.getAttribute("loggedInUser");
		if (loggedInUser != null) {
			// Tạo một Map để lưu trạng thái tham gia của người dùng với từng chuyến đi
			Map<Long, Boolean> participantStatus = new HashMap<>();
			Map<Long, Boolean> requestStatus = new HashMap<>();

			for (Trip trip : trips) {
				
				for(User user : trip.getParticipants()) {
					System.out.println("user id " + user.getId() + " loggedInUser is " + loggedInUser.getId() + " isParticipant " + trip.getParticipants().contains(loggedInUser) + " name " +user.getFullName() );
				}
				
				boolean isParticipant = trip.getParticipants().contains(loggedInUser);
				participantStatus.put(trip.getId(), isParticipant);
				
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
		model.addAttribute("user", new User());
		return "signup"; // Trả về trang signup.html
	}
	
	@PostMapping("/signup")
    public String createuser(@Valid @ModelAttribute User user, BindingResult result, @RequestParam("cccdFile") MultipartFile cccdFile,  Model model) {
        if (result.hasErrors()) {
        	model.addAttribute("user", user);
            return "signup";
        }
		try {
			String cccd = fileUploadHelper.uploadFile(cccdFile);			
			if(cccd != null) {
	    		user.setCccd(cccd);
	    	}
			userService.registerUser(user);
			System.out.println("Đăng ký thành công, hãy chờ quản trị viên phê duyệt tài khoản.");
			model.addAttribute("successMessage", "Đăng ký thành công, hãy kiểm tra email và chờ quản trị viên phê duyệt tài khoản.");
			model.addAttribute("user", new User());
			return "signup";
		} catch (EmailAlreadyExistsException e) {
			System.out.println("EmailAlreadyExistsException error");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
		model.addAttribute("user", user);
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
			userService.verifyEmail(token);
			model.addAttribute("successMessage", "Email verified successfully! You can now log in.");
		} catch (EmailVerificationException e) {
			e.printStackTrace();
			model.addAttribute("error", "Invalid verification token or the token has expired.");
		}
		return "login";
	}
}
