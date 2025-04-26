package funix.tgcp.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import funix.tgcp.exception.EmailVerificationException;
import funix.tgcp.user.UserService;

@Controller
public class ResetPasswordController {
	
	@Autowired
	private UserService userService;
	

    // Hiển thị form nhập mật khẩu mới
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        // TODO: kiểm tra token có hợp lệ không
        model.addAttribute("token", token);
        return "reset_password"; // tên view (reset_password.html)
    }

	// Xử lý submit mật khẩu mới
	@PostMapping("/reset-password")
	public String handleResetPassword(
			@RequestParam String token, 
			@RequestParam String password,
			RedirectAttributes redirectAttributes,
			Model model) {
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