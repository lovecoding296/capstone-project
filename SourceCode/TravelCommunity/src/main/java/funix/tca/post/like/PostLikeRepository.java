package funix.tca.post.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import funix.tca.appuser.AppUser;
import funix.tca.post.Post;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, AppUser user);
    long countByPost(Post post);
    void deleteByPostAndUser(Post post, AppUser user);
	List<PostLike> findByPostId(Long postId);
}
