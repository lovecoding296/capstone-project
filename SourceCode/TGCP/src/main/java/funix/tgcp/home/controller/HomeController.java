package funix.tgcp.home.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import funix.tgcp.user.City;
import funix.tgcp.user.Language;
import funix.tgcp.user.User;
import funix.tgcp.user.UserService;
import funix.tgcp.exception.EmailAlreadyExistsException;
import funix.tgcp.exception.EmailVerificationException;
import funix.tgcp.guide.service.GroupSizeCategory;
import funix.tgcp.guide.service.GuideService;
import funix.tgcp.guide.service.ServiceType;
import funix.tgcp.post.Post;
import funix.tgcp.post.PostCategory;
import funix.tgcp.post.PostService;
import funix.tgcp.util.LogHelper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	private static final LogHelper logger = new LogHelper(HomeController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private PostService postService;

	@GetMapping("/")
	public String home(Model model, HttpSession session) {

		List<Post> posts = postService.findTop4ByOrderByCreatedAtDesc();
		List<User> users = userService.findTop6UsersWithGuideServicesAndRoleGuide();
		
		
		logger.info("home users.size(); " + users.size());
		
		for (User u : users) {
			Set<City> cities = new HashSet<>();
			Set<ServiceType> types = new HashSet<>();
			Set<Language> languages = new HashSet<>();
			Set<GroupSizeCategory> groupSizes = new HashSet<>();

			for (GuideService g : u.getGuideServices()) {
				cities.add(g.getCity());
				types.add(g.getType());
				languages.add(g.getLanguage());
				groupSizes.add(g.getGroupSizeCategory());
			}
			u.setCities(cities);
			u.setServiceTypes(types);
			u.setLanguages(languages);
			u.setGroupSizes(groupSizes);
		}
		

		model.addAttribute("postCategories", PostCategory.values());
		model.addAttribute("posts", posts);
		model.addAttribute("users", users);		

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

	@GetMapping("/dashboard")
	public String dashboard() {
		return "/dashboard/dashboard";
	}

	@GetMapping("/admin/dashboard")
	public String adminDashboard() {
		return "/dashboard/admin-dashboard";
	}

	@PostMapping("/signup")
	public String createuser(@Valid @ModelAttribute User user, BindingResult result,
			@RequestParam MultipartFile cccdFile, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("user", user);
			return "signup";
		}
		String errorMessage = "";
		try {
			userService.registerUser(cccdFile, user);
			System.out.println("Đăng ký thành công, hãy chờ quản trị viên phê duyệt tài khoản.");
			model.addAttribute("successMessage",
					"Đăng ký thành công, hãy kiểm tra email và chờ quản trị viên phê duyệt tài khoản.");
			model.addAttribute("user", new User());
			return "signup";
		} catch (EmailAlreadyExistsException e) {
			errorMessage = "Email already exists, please log in!";
			e.printStackTrace();
		} catch (IOException e) {
			errorMessage = "Error occurred while uploading file. Plz try again later!";
			e.printStackTrace();
		}
		model.addAttribute("user", user);
		model.addAttribute("errorMessage", errorMessage);
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
	public String verifyEmail(@RequestParam String token, Model model) {
		try {
			userService.verifyEmail(token);
			model.addAttribute("successMessage", "Email verified successfully! You can now log in.");
		} catch (EmailVerificationException e) {
			e.printStackTrace();
			model.addAttribute("error", "Invalid verification token or the token has expired.");
		}
		return "login";
	}

	@PostMapping("/forgot-password")
	public String forgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
		userService.forgotPassword(email);
		redirectAttributes.addFlashAttribute("successMessage", "Please check your email for reset instructions!");
		return "redirect:/login";
	}
}
