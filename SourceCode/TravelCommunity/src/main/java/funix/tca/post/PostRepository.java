package funix.tca.post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthorId(Long authorId); // Tìm bài viết theo tác giả

	List<Post> findTop3ByAuthorIdOrderByCreatedAtDesc(Long id);
}
