package funix.tca.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    // Lưu bình luận
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    // Lấy bình luận theo ID
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    // Lấy tất cả bình luận
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    // Lấy bình luận theo ID bài viết
    public List<Comment> findByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    // Cập nhật bình luận
    public Comment update(Comment comment) {
        return commentRepository.save(comment);
    }

    // Xóa bình luận theo ID
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }
}
