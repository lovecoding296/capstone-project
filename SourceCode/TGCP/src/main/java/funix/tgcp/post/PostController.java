package funix.tgcp.post;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.user.User;
import funix.tgcp.user.UserService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class PostController {

	private static final Logger logger = LoggerFactory.getLogger(PostController.class);

	@Autowired
	private PostService postService;

	@Autowired
	private UserService userService;

	// Lấy danh sách tất cả bài viết
	@GetMapping()
	public String searchPosts(
			@RequestParam(required = false) String title,
			@RequestParam(required = false) String author,
			@RequestParam(required = false) PostCategory category, 
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
			Model model) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Post> posts = postService.findPostByFilter(title, author, category, pageable);
		model.addAttribute("posts", posts);
        model.addAttribute("totalPages", posts.getTotalPages()); // Số trang
        model.addAttribute("currentPage", page); // Trang hiện tại
		model.addAttribute("categories", PostCategory.values());
		
		return "post/post-list";
	}

	// Lấy bài viết theo ID
	@GetMapping("/{id}")
	public String getPostById(@PathVariable Long id, 
			Model model, 
			RedirectAttributes redirectAttributes) {
		Optional<Post> post = postService.findById(id);
		if (post.isPresent()) {
			model.addAttribute("categories", PostCategory.values());
			model.addAttribute("post", post.get());
			return "post/post-details"; // Trả về trang chi tiết bài viết
		}
		redirectAttributes.addFlashAttribute("errorMessage", "Page not found!");
		return "redirect:/error";		
	}

	// Tạo mới bài viết
	@GetMapping("/new")
	public String showCreateForm(Model model) {		

		model.addAttribute("categories", PostCategory.values());
		model.addAttribute("post", new Post());
		return "post/post-form";
	}

	@PostMapping("/new")
	public String createPost(@Valid @ModelAttribute Post post, 
			BindingResult result, 
			Model model, 
			@AuthenticationPrincipal CustomUserDetails auth) {		

		if (result.hasErrors()) {
			model.addAttribute("categories", PostCategory.values());
			return "post/post-form";
		}
		post.setAuthor(auth.getUser());
		postService.save(post);
		return "redirect:/posts";
	}

	// Chỉnh sửa bài viết
	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable Long id,
	                           Model model, 
	                           @AuthenticationPrincipal CustomUserDetails auth,
	                           RedirectAttributes redirectAttributes) {

	    Optional<Post> existingPostOptional = postService.findById(id);
	    
	    if (existingPostOptional.isEmpty()) {
	        redirectAttributes.addFlashAttribute("errorMessage", "Page not found!");
	        return "redirect:/error"; 
	    }

	    Post existingPost = existingPostOptional.get();
	    
	    if (!auth.isAdmin() && !auth.getUser().equals(existingPost.getAuthor())) {
	        redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to access this page!");
	        return "redirect:/error"; 
	    }

	    model.addAttribute("categories", PostCategory.values());
	    model.addAttribute("post", existingPost);
	    return "post/post-form";
	}


	@PostMapping("/{id}/edit")
	public String updatePost(@PathVariable Long id, 
	                         @Valid @ModelAttribute(name = "post") Post submittedPost,			
	                         BindingResult result, 
	                         Model model,
	                         @AuthenticationPrincipal CustomUserDetails auth,
	                         RedirectAttributes redirectAttributes) {

	    Optional<Post> existingPostOptional = postService.findById(id);
	    
	    if (existingPostOptional.isEmpty()) {
	        redirectAttributes.addFlashAttribute("errorMessage", "Page not found!");
	        return "redirect:/error";
	    }

	    Post existingPost = existingPostOptional.get();
	    
	    if (!auth.isAdmin() && !existingPost.getAuthor().equals(auth.getUser())) {
	        redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to access this page!");
	        return "redirect:/error";
	    }

	    if (result.hasErrors()) {
	        model.addAttribute("categories", PostCategory.values());
	        model.addAttribute("post", submittedPost);
	        return "post/post-form";
	    }

	    Post updatedPost = postService.update(id, submittedPost);
	    model.addAttribute("categories", PostCategory.values());
	    model.addAttribute("post", updatedPost);
	    return "post/post-details";	
	}

}
