package funix.tgcp.post;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.util.LogHelper;

@RestController
public class PostRestController {

	private static final LogHelper logger = new LogHelper(PostRestController.class);

	@Autowired
	private PostService postService;
	
	@GetMapping("/api/users/posts")
    public Page<Post> findPostByCurrentUserAndByFilter(
    		@RequestParam(required = false) String title,
	        @RequestParam(required = false) PostCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @AuthenticationPrincipal CustomUserDetails auth
	        ) {    	
    	
		logger.info("auth " + auth);
		
		Pageable pageable = PageRequest.of(page, size);
		
    	return postService.findPostByCurrentUserAndByFilter(
    			auth.getId(),
    			title,
    			category,
    			pageable
    			);
    }
	
	@GetMapping("/api/admin/posts")
    public Page<Post> findPosttByFilter(
    		@RequestParam(required = false) String title,
	        @RequestParam(required = false) String author,
	        @RequestParam(required = false) PostCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
	        ) {    	
    	
		Pageable pageable = PageRequest.of(page, size);
    	return postService.findPostByFilter(
    			title,
    			author,
    			category,
    			pageable
    			);
    }
	
	
	@DeleteMapping("/api/posts/{id}/delete")
	public ResponseEntity<Void> deleteById(
			@PathVariable Long id,
			@AuthenticationPrincipal CustomUserDetails auth) {
		Optional<Post> postOpt = postService.findById(id);
	    if (postOpt.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 nếu không tìm thấy bài viết
	    }
	    
	    Post post = postOpt.get();
	    
	    if(auth == null) {
	    	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }

	    // Kiểm tra quyền sở hữu bài viết
	    if (!auth.isAdmin() && !post.getAuthor().equals(auth.getUser())) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 nếu người dùng không phải chủ sở hữu bài viết
	    }

	    postService.deleteById(id); // Xóa bài viết
	    return ResponseEntity.noContent().build(); // 204 No Content (không có dữ liệu trả về)
	}

	
	
	
	
	
}
