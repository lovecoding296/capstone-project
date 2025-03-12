package funix.tca.controller;

import funix.tca.entity.Comment;
import funix.tca.entity.Post;
import funix.tca.service.CommentService;
import funix.tca.service.PostService;
import funix.tca.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private AppUserService appUserService;

    // Lấy tất cả bình luận của một bài viết
    @GetMapping("/post/{postId}")
    public String getCommentsByPost(@PathVariable Long postId, Model model) {
        List<Comment> comments = commentService.findByPostId(postId);
        model.addAttribute("comments", comments);
        model.addAttribute("postId", postId);
        return "comment/list"; // Trả về trang danh sách bình luận của bài viết
    }

    // Tạo mới bình luận
    @GetMapping("/new/{postId}")
    public String showCreateForm(@PathVariable Long postId, Model model) {
        Comment comment = new Comment();
        model.addAttribute("comment", comment);
        model.addAttribute("postId", postId); // Truyền ID bài viết để tạo bình luận
        model.addAttribute("users", appUserService.findAll()); // Cung cấp danh sách người dùng
        return "comment/form"; // Trả về trang tạo bình luận mới
    }

    @PostMapping("/new/{postId}")
    public String createComment(@PathVariable Long postId, @Valid @ModelAttribute Comment comment, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("postId", postId);
            model.addAttribute("users", appUserService.findAll());
            return "comment/form"; // Trả về lại trang tạo bình luận nếu có lỗi
        }
        
        // Thiết lập bài viết và người bình luận
        Post post = postService.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        comment.setPost(post);

        // Lưu bình luận
        commentService.save(comment);
        return "redirect:/comments/post/{postId}"; // Chuyển hướng về danh sách bình luận của bài viết
    }

    // Xóa bình luận
    @GetMapping("/{id}/delete")
    public String deleteComment(@PathVariable Long id) {
        commentService.deleteById(id);
        return "redirect:/comments"; // Chuyển hướng về trang danh sách bình luận
    }
}
