package funix.tgcp.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthorId(Long authorId); // Tìm bài viết theo tác giả

	List<Post> findTop3ByAuthorIdOrderByCreatedAtDesc(Long id);
	
	List<Post> findTop4ByOrderByCreatedAtDesc();

	
    @Query("SELECT p FROM Post p WHERE " +
           "(:title IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR LOWER(p.author.fullName) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:category IS NULL OR p.category = :category)")
    Page<Post> findPostByFilter(@Param("title") String title,
                             @Param("author") String author,
                             @Param("category") PostCategory category,
                             Pageable pageable);
    
    
    
 

}
