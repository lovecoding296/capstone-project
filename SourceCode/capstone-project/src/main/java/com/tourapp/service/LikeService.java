package funix.tca.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import funix.tca.entity.Like;
import funix.tca.entity.Post;
import funix.tca.entity.AppUser;
import funix.tca.repository.LikeRepository;
import java.util.Optional;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;

    public boolean toggleLike(Post post, AppUser user) {
        Optional<Like> existingLike = likeRepository.findByPostAndUser(post, user);
        if (existingLike.isPresent()) {
            likeRepository.deleteByPostAndUser(post, user);
            return false; // Đã bỏ thích
        } else {
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
            return true; // Đã thích
        }
    }

    public long countLikes(Post post) {
        return likeRepository.countByPost(post);
    }
}
