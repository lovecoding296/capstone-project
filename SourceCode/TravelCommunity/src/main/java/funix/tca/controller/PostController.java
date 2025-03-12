package funix.tca.controller;

import funix.tca.entity.AppUser;
import funix.tca.entity.Post;
import funix.tca.service.PostService;
import funix.tca.service.AppUserService;

import jakarta.servlet.http.HttpSession;
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

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService postService;

    @Autowired
    private AppUserService appUserService;

    // Lấy danh sách tất cả bài viết
    @GetMapping()
    public String getAllPosts(Model model) {
        model.addAttribute("posts", postService.findAll());
        return "post/list"; // Trả về trang danh sách bài viết
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
    public String showCreateForm(Model model, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
        }

        model.addAttribute("post", new Post());
        model.addAttribute("users", appUserService.findAll());
        return "post/form"; // Trả về trang tạo bài viết mới
    }

    @PostMapping("/new")
    public String createPost(@Valid @ModelAttribute Post post, BindingResult result, Model model, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Kiểm tra đăng nhập
        }

        if (result.hasErrors()) {
            model.addAttribute("users", appUserService.findAll());
            return "post/form";
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
            model.addAttribute("post", post.get());
            model.addAttribute("users", appUserService.findAll());
            return "post/form";
        }
        return "redirect:/posts";
    }

    @PostMapping("/{id}/edit")
    public String updatePost(@PathVariable Long id, @Valid @ModelAttribute Post post, BindingResult result, Model model, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Kiểm tra đăng nhập
        }

        if (result.hasErrors()) {
            model.addAttribute("users", appUserService.findAll());
            return "post/form";
        }

        post.setId(id);
        postService.update(post);
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
