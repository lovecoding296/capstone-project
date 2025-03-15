package funix.tca.comment;

import funix.tca.appuser.AppUser;
import funix.tca.appuser.AppUserService;
import funix.tca.post.Post;
import funix.tca.post.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

	@Autowired
	private CommentService commentService;

	@Autowired
	private PostService postService;

	@Autowired
	private AppUserService appUserService;

	// Lấy tất cả bình luận của một bài viết
	@GetMapping("/post/{postId}")
	public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
		List<Comment> comments = commentService.findByPostId(postId);

		for (Comment comment : comments) {
			System.out.println("comment " + comment.getCommenter().getFullName());
		}

		return ResponseEntity.ok(comments);
	}

	// Tạo mới bình luận
	@PostMapping("/post/{postId}")
	public ResponseEntity<Comment> createComment(@PathVariable Long postId, @RequestBody @Valid Comment comment,
			HttpSession session) {
		Post post = postService.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
		comment.setPost(post);
		AppUser user = (AppUser) session.getAttribute("loggedInUser");
		comment.setCommenter(user);
		Comment savedComment = commentService.save(comment);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
	}

	// Xóa bình luận
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
		commentService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
