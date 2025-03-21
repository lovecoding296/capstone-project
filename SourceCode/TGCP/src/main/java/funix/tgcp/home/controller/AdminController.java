package funix.tgcp.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
	
	@GetMapping("/admin")
	public String admin(HttpSession session, Model model) {
		return "admin/admin-dashboard";
	}
	
	@GetMapping("/admin/users")
	public String listUsers(HttpSession session, Model model) {
		return "admin/admin-dashboard";
	}
	
	@GetMapping("/admin/posts")
	public String listPosts(HttpSession session, Model model) {
		return "admin/admin-dashboard";
	}
	
	@GetMapping("/admin/tours")
	public String listTours(HttpSession session, Model model) {
		return "admin/admin-dashboard";
	}
	
	@GetMapping("/admin/tour-requests")
	public String listTourRequests(HttpSession session, Model model) {
		return "admin/admin-dashboard";
	}
	
	@GetMapping("/admin/comments")
	public String listComments(HttpSession session, Model model) {
		return "admin/admin-dashboard";
	}
	
	@GetMapping("/admin/reports")
	public String listReports(HttpSession session, Model model) {
		return "admin/admin-dashboard";
	}
	
	@GetMapping("/admin/verifys")
	public String verify(HttpSession session, Model model) {
		return "admin/admin-dashboard";
	}
	
	
	/*
	 * @GetMapping("/admin/posts") public String posts(HttpSession session, Model
	 * model) { return "admin/admin-dashboard"; }
	 * 
	 * @GetMapping("/admin/tours") public String tours(HttpSession session, Model
	 * model) { return "admin/admin-dashboard"; }
	 */

}
