package funix.tca.service;

import funix.tca.entity.Post;
import funix.tca.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    // Lưu bài viết
    public Post save(Post post) {
        return postRepository.save(post);
    }

    // Lấy bài viết theo ID
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    // Lấy tất cả bài viết
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    // Lấy bài viết của một tác giả (AppUser)
    public List<Post> findByAuthorId(Long authorId) {
        return postRepository.findByAuthorId(authorId);
    }

    // Cập nhật bài viết
    public Post update(Post post) {
        return postRepository.save(post);
    }

    // Xóa bài viết theo ID
    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }
}
