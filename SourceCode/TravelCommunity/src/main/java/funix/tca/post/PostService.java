package funix.tca.post;

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
    public boolean update(Long id, Post post) {
        return postRepository.findById(id).map(currentPost -> {
        	if (post.getContent() != null && !post.getContent().isBlank()) {
                currentPost.setContent(post.getContent());
            }
            if (post.getTitle() != null && !post.getTitle().isBlank()) {
                currentPost.setTitle(post.getTitle());
            }
            postRepository.save(currentPost);
            return true;
        }).orElseGet(() -> {
            System.out.println("Cannot find post with id: " + id);
            return false;
        });
    }
    
    public List<Post> getLatestPosts() {
        return postRepository.findTop4ByOrderByCreatedAtDesc();
    }

    // Xóa bài viết theo ID
    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

	public List<Post> findTop3ByAuthorIdOrderByCreatedAtDesc(Long id) {
		return postRepository.findTop3ByAuthorIdOrderByCreatedAtDesc(id);
	}

	public List<Post> searchPosts(String title, String author, PostCategory category) {
		return postRepository.findByFilters(title, author, category);
	}
}
