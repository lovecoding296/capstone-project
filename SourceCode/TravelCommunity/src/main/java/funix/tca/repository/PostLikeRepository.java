package funix.tca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import funix.tca.entity.Post;
import funix.tca.entity.PostLike;
import funix.tca.entity.AppUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, AppUser user);
    long countByPost(Post post);
    void deleteByPostAndUser(Post post, AppUser user);
	List<PostLike> findByPostId(Long postId);
}
