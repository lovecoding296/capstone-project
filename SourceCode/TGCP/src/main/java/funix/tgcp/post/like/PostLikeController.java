package funix.tgcp.post.like;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import funix.tgcp.appuser.AppUser;
import funix.tgcp.appuser.AppUserService;
import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.post.Post;
import funix.tgcp.post.PostService;
import funix.tgcp.review.ReviewController;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/likes")
public class PostLikeController {
	private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

	
    @Autowired
    private PostLikeService postLikeService;
    
    @Autowired
    private PostService postService;

    @Autowired
    private AppUserService userService;

    @PostMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> likePost(@PathVariable Long postId, HttpSession session) {

    	AppUser  user = (AppUser) session.getAttribute("loggedInUser");
    	
    	if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User chưa đăng nhập"));
        }
    	
        Optional<Post> optionalPost = postService.findById(postId);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Bài viết không tồn tại"));
        }
        
        Post post = optionalPost.get();

        boolean liked = postLikeService.toggleLike(post, user);
        List<PostLike> likes =  postLikeService.findByPostId(postId);
        
        
     // Trả về số lượt thích và trạng thái thích của user
        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", likes.size()); // Số lượt thích
        response.put("liked", liked); // Trạng thái user đã thích hay chưa

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}/count")
    public ResponseEntity<Long> countLikes(@PathVariable Long postId) {
        Optional<Post> post = postService.findById(postId);
        long likeCount = postLikeService.countLikes(post.get());
        return ResponseEntity.ok(likeCount);
    }
}
