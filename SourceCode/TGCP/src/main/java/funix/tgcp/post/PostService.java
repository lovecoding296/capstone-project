package funix.tgcp.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import funix.tgcp.report.Report;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepo;

    // Lưu bài viết
    public Post save(Post post) {
        return postRepo.save(post);
    }

    // Lấy bài viết theo ID
    public Optional<Post> findById(Long id) {
        return postRepo.findById(id);
    }

    // Lấy tất cả bài viết
    public List<Post> findAll() {
        return postRepo.findAll();
    }

    // Lấy bài viết của một tác giả (User)
    public List<Post> findByAuthorId(Long authorId) {
        return postRepo.findByAuthorId(authorId);
    }

    // Cập nhật bài viết
    public boolean update(Long id, Post post) {
        return postRepo.findById(id).map(currentPost -> {
        	if (post.getContent() != null && !post.getContent().isBlank()) {
                currentPost.setContent(post.getContent());
            }
            if (post.getTitle() != null && !post.getTitle().isBlank()) {
                currentPost.setTitle(post.getTitle());
            }
            postRepo.save(currentPost);
            return true;
        }).orElseGet(() -> {
            System.out.println("Cannot find post with id: " + id);
            return false;
        });
    }
    
    public List<Post> findTop4ByOrderByCreatedAtDesc() {
        return postRepo.findTop4ByOrderByCreatedAtDesc();
    }

    // Xóa bài viết theo ID
    public void deleteById(Long id) {
    	postRepo.deleteById(id);
    }

	public List<Post> findTop3ByAuthorIdOrderByCreatedAtDesc(Long id) {
		return postRepo.findTop3ByAuthorIdOrderByCreatedAtDesc(id);
	}

//	public Page<Post> searchPosts(String title, String author, PostCategory category) {
//		return postRepo.findPostByFilter(title, author, category);
//	}
	
	public Page<Post> findPostByCurrentUserAndByFilter(Long userId, String title, PostCategory category, Pageable pageable) {
		return postRepo.findPostByCurrentUserAndByFilter(userId, title, category, pageable);
	}
	
	public Page<Post> findPostByFilter(String title, String author, PostCategory category, Pageable pageable) {
		return postRepo.findPostByFilter(title, author, category, pageable);
	}


}
