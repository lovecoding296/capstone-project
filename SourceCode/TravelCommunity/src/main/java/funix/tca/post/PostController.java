package funix.tca.post;

import funix.tca.appuser.AppUser;
import funix.tca.appuser.AppUserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Locale.Category;
import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class PostController {

	private static final Logger logger = LoggerFactory.getLogger(PostController.class);

	@Autowired
	private PostService postService;

	@Autowired
	private AppUserService appUserService;

	// Lấy danh sách tất cả bài viết
	@GetMapping()
	public String getAllPosts(Model model) {
		model.addAttribute("posts", postService.findAll());
		return "post/post-list"; // Trả về trang danh sách bài viết
	}

	// Lấy bài viết theo current user ID
	@GetMapping("/author/{id}")
	public String getPostByAppUserId(@PathVariable Long id, Model model) {
		List<Post> posts = postService.findByAuthorId(id);
		model.addAttribute("posts", posts);
		return "post/post-list"; // Trả về trang danh sách bài viết
	}

	// Lấy bài viết theo ID
	@GetMapping("/{id}")
	public String getPostById(@PathVariable Long id, Model model) {
		Optional<Post> post = postService.findById(id);
		if (post.isPresent()) {
			model.addAttribute("categories", PostCategory.values());
			model.addAttribute("post", post.get());
			return "post/post-details"; // Trả về trang chi tiết bài viết
		}
		return "redirect:/posts"; // Nếu không tìm thấy, chuyển hướng về trang danh sách
	}

	@GetMapping("/search")
	public String searchPosts(@RequestParam(name = "title", required = false) String title,
			@RequestParam(name = "author", required = false) String author,
			@RequestParam(name = "category", required = false) PostCategory category, Model model) {

		List<Post> searchResults = postService.searchPosts(title, author, category);
		model.addAttribute("posts", searchResults);
		return "post/post-list"; // Trả về trang danh sách bài viết
	}

	// Tạo mới bài viết
	@GetMapping("/new")
	public String showCreateForm(Model model, HttpSession session) {
		AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
		}

		model.addAttribute("categories", PostCategory.values());
		model.addAttribute("post", new Post());
		// model.addAttribute("users", appUserService.findAll());
		return "post/post-form"; // Trả về trang tạo bài viết mới
	}

	@PostMapping("/new")
	public String createPost(@Valid @ModelAttribute Post post, BindingResult result, Model model, HttpSession session) {
		AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			return "redirect:/login"; // Kiểm tra đăng nhập
		}

		if (result.hasErrors()) {
			model.addAttribute("users", appUserService.findAll());
			return "post/post-form";
		}
		post.setAuthor(loggedInUser);
		postService.save(post);
		return "redirect:/posts"; // Chuyển hướng sau khi lưu
	}

	// Chỉnh sửa bài viết
	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
		AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			return "redirect:/login"; // Kiểm tra đăng nhập
		}

		Optional<Post> post = postService.findById(id);
		if (post.isPresent()) {
			model.addAttribute("categories", PostCategory.values());
			model.addAttribute("post", post.get());
			model.addAttribute("users", appUserService.findAll());
			return "post/post-form";
		}
		return "redirect:/posts";
	}

	@PostMapping("/{id}/edit")
	public String updatePost(@PathVariable Long id, @Valid @ModelAttribute Post post, BindingResult result, Model model,
			HttpSession session) {
		AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			return "redirect:/login"; // Kiểm tra đăng nhập
		}

		if (result.hasErrors()) {
			model.addAttribute("users", appUserService.findAll());
			return "post/post-form";
		}

		postService.update(id, post);
		return "redirect:/posts";
	}

	// Xóa bài viết theo ID
	@GetMapping("/{id}/delete")
	public String deletePost(@PathVariable Long id, HttpSession session) {
		AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			return "redirect:/login"; // Kiểm tra đăng nhập
		}

		postService.deleteById(id);
		return "redirect:/posts";
	}
}
