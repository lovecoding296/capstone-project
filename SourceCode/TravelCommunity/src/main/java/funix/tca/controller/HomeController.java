package funix.tca.controller;

import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import funix.tca.entity.AppUser;
import funix.tca.entity.Post;
import funix.tca.entity.Trip;
import funix.tca.exception.EmailVerificationException;
import funix.tca.service.AppUserService;
import funix.tca.service.PostService;
import funix.tca.service.TripService;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

	@Autowired
	private AppUserService appUserService;

	@Autowired
	private TripService tripService;

	@Autowired
	private PostService postService;

	@GetMapping("/")
	public String home(Model model) {
		logger.info("home");
		List<Trip> trips = tripService.findAll();
		List<Post> posts = postService.findAll();

		// Đảm bảo likes & comments không null
		for (Post post : posts) {
			logger.info("post.likes " + post.getLikes().size());
			
			if (post.getLikes() == null) {
				post.setLikes(new HashSet<>()); // Tránh lỗi null
			}
			if (post.getComments() == null) {
				post.setComments(new HashSet<>()); // Tránh lỗi null
			}
		}
		
		

		model.addAttribute("trips", trips);
		model.addAttribute("posts", posts);
		return "home";
	}

	@GetMapping("/signup")
	public String showSignupForm(HttpSession session, Model model) {
		if (session.getAttribute("loggedInUser") != null) {
			return "redirect:/"; // Nếu đã login, chuyển sang home
		}
		model.addAttribute("user", new AppUser());
		return "signup"; // Trả về trang signup.html
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
