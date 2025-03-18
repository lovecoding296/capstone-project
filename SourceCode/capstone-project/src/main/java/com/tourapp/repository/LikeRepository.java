package funix.tca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import funix.tca.entity.Like;
import funix.tca.entity.Post;
import funix.tca.entity.AppUser;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByPostAndUser(Post post, AppUser user);
    long countByPost(Post post);
    void deleteByPostAndUser(Post post, AppUser user);
}
