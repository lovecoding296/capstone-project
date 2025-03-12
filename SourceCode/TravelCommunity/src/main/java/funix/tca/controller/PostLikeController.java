package funix.tca.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import funix.tca.entity.Post;
import funix.tca.config.CustomUserDetails;
import funix.tca.entity.AppUser;
import funix.tca.service.PostLikeService;
import funix.tca.service.PostService;
import jakarta.servlet.http.HttpSession;
import funix.tca.service.AppUserService;

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
        Optional<Post> post = postService.findById(postId);
        //Optional<AppUser> user = userService.findById(userId);

        boolean liked = postLikeService.toggleLike(post.get(), user);
        
        
        
     // Trả về số lượt thích và trạng thái thích của user
        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", post.get().getLikes().size()); // Số lượt thích
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
