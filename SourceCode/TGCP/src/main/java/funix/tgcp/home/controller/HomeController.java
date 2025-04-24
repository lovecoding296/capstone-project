package funix.tgcp.home.controller;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.user.Role;
import funix.tgcp.user.User;
import funix.tgcp.user.UserService;
import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.exception.EmailAlreadyExistsException;
import funix.tgcp.exception.EmailVerificationException;
import funix.tgcp.post.Post;
import funix.tgcp.post.PostCategory;
import funix.tgcp.post.PostService;
import funix.tgcp.util.FileHelper;
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
	
	@Autowired
	private FileHelper fileHelper;

	@GetMapping("/")
	public String home(Model model, HttpSession session) {
		
		logger.info("home");
		List<Post> posts = postService.getLatestPosts();
		List<User> users = userService.getTopByRoleOrderByAverageRatingDesc(Role.ROLE_GUIDE);
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
	
	
	@GetMapping("/users/bookings/{bookingId}")
	public String showBooking() {
		return "booking/user-booking";
	}
	
	@GetMapping("/guides/bookings/{bookingId}")
	public String showBookingForGuide() {
		return "booking/guide-booking";
	}
	
	@GetMapping("/tours/{id}/payments")
	public String showPayment() {
		return "payment/payment"; 
	}

	
	@GetMapping("/dashboard")
	public String dashboard() {
		return "/dashboard/dashboard-main";
	}
	
	@GetMapping("/dashboard/guide-approval")
	public String dashboardGuideApproval() {
		return "/dashboard/guide-approval";
	}
	
	@PostMapping("/signup")
    public String createuser(@Valid @ModelAttribute User user, BindingResult result, @RequestParam("cccdFile") MultipartFile cccdFile,  Model model) {
        if (result.hasErrors()) {
        	model.addAttribute("user", user);
            return "signup";
        }
		try {
			String cccd = fileHelper.uploadFile(cccdFile);			
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
