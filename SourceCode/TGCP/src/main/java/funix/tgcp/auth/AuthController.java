package funix.tgcp.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.exception.EmailAlreadyExistsException;
import funix.tgcp.exception.EmailVerificationException;
import funix.tgcp.home.HomeController;
import funix.tgcp.user.User;
import funix.tgcp.user.UserService;
import funix.tgcp.user.request.UserRequest;
import funix.tgcp.user.request.UserRequestService;
import funix.tgcp.util.LogHelper;
import jakarta.validation.Valid;

@Controller
public class AuthController {

	private static final LogHelper logger = new LogHelper(AuthController.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRequestService userRequestService;

	@GetMapping("/signup")
	public String showSignupForm(@AuthenticationPrincipal CustomUserDetails auth, Model model) {
		if (auth != null) {
			return "redirect:/"; // Nếu đã login, chuyển sang home
		}
		model.addAttribute("user", new UserRequest());
		return "signup"; // Trả về trang signup.html
	}

	@GetMapping("/login")
	public String login(@AuthenticationPrincipal CustomUserDetails auth) {
		if (auth != null) {
			return "redirect:/"; // Nếu đã login, chuyển sang home
		}
		return "login";
	}

	@PostMapping("/signup")
	public String createUser(@Valid @ModelAttribute UserRequest user, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("user", user);
			return "signup";
		}
		String errorMessage = "";
		try {
			userRequestService.registerUser(user);
			System.out.println("Đăng ký thành công, hãy chờ quản trị viên phê duyệt tài khoản.");
			model.addAttribute("successMessage",
					"Registration successful, please check your email and wait for the administrator to approve your account.");
			model.addAttribute("user", new UserRequest());
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

	@GetMapping("/verify")
	public String verifyEmail(@RequestParam String token, Model model) {
		try {
			userRequestService.verifyEmail(token);
			model.addAttribute("successMessage", "Email verified successfully! Please wait for the administrator to approve your account.");
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

	// Hiển thị form nhập mật khẩu mới
	@GetMapping("/reset-password")
	public String showResetPasswordForm(@RequestParam String token, Model model) {
		// TODO: kiểm tra token có hợp lệ không
		model.addAttribute("token", token);
		return "reset_password"; // tên view (reset_password.html)
	}

	// Xử lý submit mật khẩu mới
	@PostMapping("/reset-password")
	public String handleResetPassword(@RequestParam String token, @RequestParam String password,
			RedirectAttributes redirectAttributes, Model model) {
		try {
			userService.handleResetPassword(token, password);
		} catch (EmailVerificationException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			e.printStackTrace();
			return "redirect:/login";
		}
		redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
		return "redirect:/login";
	}

}
