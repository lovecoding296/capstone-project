package funix.tgcp.post.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import funix.tgcp.user.User;
import funix.tgcp.post.Post;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);
    long countByPost(Post post);
    void deleteByPostAndUser(Post post, User user);
	List<PostLike> findByPostId(Long postId);
}
