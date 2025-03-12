package funix.tca.controller;

import funix.tca.entity.AppUser;
import funix.tca.entity.Post;
import funix.tca.service.PostService;
import funix.tca.service.AppUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class PostController {
	
	private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);


    @Autowired
    private PostService postService;

    @Autowired
    private AppUserService appUserService;

    // Lấy danh sách tất cả bài viết
    @GetMapping()
    public String getAllPosts(Model model) {
        model.addAttribute("posts", postService.findAll());
        return "post/list"; // Trả về trang Thymeleaf với danh sách bài viết
    }

    // Lấy bài viết theo ID
    @GetMapping("/{id}")
    public String getPostById(@PathVariable Long id, Model model) {
        Optional<Post> post = postService.findById(id);
        if (post.isPresent()) {
            model.addAttribute("post", post.get());
            return "post/detail"; // Trả về trang chi tiết bài viết
        }
        return "redirect:/posts"; // Nếu không tìm thấy, chuyển hướng về trang danh sách
    }

    // Tạo mới bài viết
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("users", appUserService.findAll()); // Cung cấp danh sách người dùng cho tác giả bài viết
        return "post/form"; // Trả về trang tạo bài viết mới
    }

    @PostMapping("/new")
    public String createPost(@Valid @ModelAttribute Post post, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", appUserService.findAll()); // Cung cấp lại danh sách người dùng nếu có lỗi
            return "post/form"; // Trả về lại trang tạo bài viết nếu có lỗi
        }
        postService.save(post);
        return "redirect:/posts"; // Chuyển hướng đến danh sách bài viết sau khi lưu
    }

    // Chỉnh sửa bài viết
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Post> post = postService.findById(id);
        if (post.isPresent()) {
            model.addAttribute("post", post.get());
            model.addAttribute("users", appUserService.findAll()); // Cung cấp danh sách người dùng cho tác giả bài viết
            return "post/form"; // Trả về trang chỉnh sửa bài viết
        }
        return "redirect:/posts"; // Nếu không tìm thấy, chuyển hướng về trang danh sách
    }

    @PostMapping("/{id}/edit")
    public String updatePost(@PathVariable Long id, @Valid @ModelAttribute Post post, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", appUserService.findAll());
            return "post/form"; // Trả về lại trang chỉnh sửa nếu có lỗi
        }
        post.setId(id);
        postService.update(post);
        return "redirect:/posts"; // Chuyển hướng đến danh sách bài viết sau khi cập nhật
    }

    // Xóa bài viết theo ID
    @GetMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.deleteById(id);
        return "redirect:/posts"; // Chuyển hướng về trang danh sách sau khi xóa
    }
}
