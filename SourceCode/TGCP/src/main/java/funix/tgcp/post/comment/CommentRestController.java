package funix.tgcp.post.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import funix.tgcp.user.User;
import funix.tgcp.user.UserService;
import funix.tgcp.util.LogHelper;
import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.notification.Notification;
import funix.tgcp.notification.NotificationService;
import funix.tgcp.post.Post;
import funix.tgcp.post.PostService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {

	private static final LogHelper logger = new LogHelper(CommentRestController.class);

	@Autowired
	private CommentService commentService;

	@Autowired
	private PostService postService;

	@Autowired
	private UserService userService;

	@Autowired
	private NotificationService notificationService;

	// Lấy tất cả bình luận của một bài viết
	@GetMapping("/post/{postId}")
	public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
		logger.info("");
		List<Comment> comments = commentService.findByPostId(postId);
		List<Comment> tree = buildCommentTree(comments);

		return ResponseEntity.ok(tree);
	}

	// Tạo mới bình luận
	@PostMapping("/post/{postId}")
	public ResponseEntity<Comment> createComment(
			@PathVariable Long postId, 
			@RequestBody @Valid Comment comment,
			@AuthenticationPrincipal CustomUserDetails auth) {

		logger.info("auth " + auth);

		User currentUser = new User();
		if (auth != null) {
			currentUser.setId(auth.getId());

		} else {
			currentUser.setId((long) 1);
		}

		Post post = postService.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
		comment.setPost(post);
		comment.setCommenter(currentUser);

		if (!post.getAuthor().equals(currentUser)) {

			logger.info(auth.getFullName() + " commented on your post!");
						
			notificationService.sendNotification(
					post.getAuthor(),
					auth.getFullName() + " commented on your post (" + post.getTitle() + ")!",
					"/posts/" + postId
					);
			
		}

		Comment savedComment = commentService.save(comment);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
	}
	
	@PostMapping("/reply/{commentId}")
	public ResponseEntity<Comment> createReplyComment(
	        @PathVariable Long commentId,
	        @RequestBody @Valid Comment comment,
	        @AuthenticationPrincipal CustomUserDetails auth) {

	    logger.info("auth: {}", auth);

	    Long currentUserId = (auth != null) ? auth.getId() : 1L;
	    String currentUserName = (auth != null) ? auth.getFullName() : "Người dùng";

	    User currentUser = new User();
	    currentUser.setId(currentUserId);

	    Optional<Comment> optionalParent = commentService.findById(commentId);
	    if (optionalParent.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }

	    Comment parentComment = optionalParent.get();
	    Post post = parentComment.getPost();

	    comment.setPost(post);
	    comment.setCommenter(currentUser);
	    comment.setContent("<b>"+ parentComment.getCommenter().getFullName() + "</b> " + comment.getContent());
	    
	    if(parentComment.getParent() != null) {
	    	comment.setParent(parentComment.getParent());
	    } else {
	    	comment.setParent(parentComment);
	    }
	    
	    

	    // Gửi thông báo cho người được trả lời (nếu không phải chính mình)
	    User parentAuthor = parentComment.getCommenter();
	    if (!parentAuthor.getId().equals(currentUserId)) {
	    	notificationService.sendNotification(
	            parentAuthor,
	            currentUserName + " replied to your comment!",
	            "/posts/" + post.getId()
	        );
	    }

	    // Gửi thông báo cho tác giả bài viết (nếu không phải chính mình hoặc người được trả lời)
	    User postAuthor = post.getAuthor();
	    if (!postAuthor.getId().equals(currentUserId) && !postAuthor.getId().equals(parentAuthor.getId())) {
	    	notificationService.sendNotification(
	            postAuthor,
	            currentUserName + " commented on your post (" + post.getTitle() + ")!",
	            "/posts/" + post.getId()
	        );
	    }

	    Comment savedComment = commentService.save(comment);
	    return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
	}


	


	// Xóa bình luận
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
		commentService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	
	public List<Comment> buildCommentTree(List<Comment> comments) {
	    List<Comment> roots = new ArrayList<>();
	    
	    for (Comment c : comments) {
	    	
	        if (c.getParent() != null) {
	            Comment parent = c.getParent();
	            if (parent != null) {
	                parent.getReplies().add(c);
	            }
	        } else {
	            roots.add(c);
	        }
	        
	        c.setParent(null);
	    }

	    return roots;
	}
	
	public void sendNotification() {
		
	}
}
