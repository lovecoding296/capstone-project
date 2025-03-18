package funix.tgcp.post.like;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import funix.tgcp.appuser.AppUser;
import funix.tgcp.post.Post;
import funix.tgcp.review.ReviewController;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostLikeService {
	private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

	
    @Autowired
    private PostLikeRepository likeRepository;

    @Transactional
    public boolean toggleLike(Post post, AppUser user) {
    	
    	logger.info("toggleLike " + post.toString() + " " + user.toString());
    	
        Optional<PostLike> existingLike = likeRepository.findByPostAndUser(post, user);
        if (existingLike.isPresent()) {
            likeRepository.deleteByPostAndUser(post, user);
            return false; // Đã bỏ thích
        } else {
        	PostLike like = new PostLike();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
            return true; // Đã thích
        }
    }

    public long countLikes(Post post) {
        return likeRepository.countByPost(post);
    }

	public List<PostLike> findByPostId(Long postId) {
		return likeRepository.findByPostId(postId);
	}
}
