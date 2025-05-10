package funix.tgcp.post.like;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import funix.tgcp.user.User;
import funix.tgcp.booking.review.ReviewRestController;
import funix.tgcp.notification.NotificationService;
import funix.tgcp.post.Post;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostLikeService {
	private static final Logger logger = LoggerFactory.getLogger(ReviewRestController.class);

	
    @Autowired
    private PostLikeRepository likeRepository;
    
    @Autowired
    private NotificationService notificationService;

    @Transactional
    public boolean toggleLike(Post post, User user) {
    	
    	logger.info("toggleLike " + post.toString() + " " + user.toString());
    	
        Optional<PostLike> existingLikeOp = likeRepository.findByPostAndUser(post, user);
        if (existingLikeOp.isPresent()) {
        	PostLike existingLike = existingLikeOp.get();
        	existingLike.setLiked(!existingLike.isLiked());
            likeRepository.save(existingLike);
            return false; // Đã bỏ thích
        } else {
        	PostLike like = new PostLike();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
            
            
            notificationService.sendNotification(
            		post.getAuthor(), 
            		user.getFullName() + " likded your post (" + post.getTitle() + ")!",
            		"/posts/" + post.getId());
            
            return true; // Đã thích
        }
    }

    public long countLikes(Post post) {
        return likeRepository.countByPostAndIsLikedTrue(post);
    }

	public List<PostLike> findByPostId(Long postId) {
		return likeRepository.findByPostId(postId);
	}
}
