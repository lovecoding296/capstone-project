package funix.tgcp.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId); // Lấy các bình luận theo ID bài viết
    List<Comment> findByCommenterId(Long userId); // Lấy các bình luận của người dùng
}
