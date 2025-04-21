package funix.tgcp.user;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import funix.tgcp.post.Post;
import funix.tgcp.post.PostService;
import funix.tgcp.review.Review;
import funix.tgcp.review.ReviewService;
import funix.tgcp.util.FileHelper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private PostService postService;
    
	@Autowired
    private ReviewService reviewService;
	
	@Autowired
	private FileHelper fileHelper;
    
    
    
    // Lấy danh sách tất cả người dùng
    @GetMapping("/users")
    public String getAllusers(Model model) {
        List<User> users = userService.findAll();
        
        
        model.addAttribute("users", users);
        return "user/user-list"; // Trả về trang Thymeleaf với danh sách người dùng
    }

    // Lấy thông tin người dùng theo ID
    @GetMapping("/users/{id}")
    public String getuserById(@PathVariable Long id, Model model) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
        	
            List<Post> posts = postService.findTop3ByAuthorIdOrderByCreatedAtDesc(user.get().getId());            
            List<Review> reviews = reviewService.findTop3ByReviewedUserIdOrderByReviewDateDesc(user.get().getId());

        	
            model.addAttribute("user", user.get());
            model.addAttribute("posts", posts);
            model.addAttribute("reviews", reviews);
            return "user/user-details"; // Trả về trang chi tiết người dùng
        }
        return "redirect:/users"; // Nếu không tìm thấy, chuyển hướng về trang danh sách
    }

    // Tạo mới người dùng
    @GetMapping("/users/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "user/form"; // Trả về trang Thymeleaf để tạo người dùng mới
    }

    @PostMapping("/users/new")
    public String createuser(@Valid @ModelAttribute User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user/user-form"; // Trả về lại trang tạo nếu có lỗi
        }
        userService.save(user);
        return "redirect:/users"; // Chuyển hướng đến danh sách người dùng sau khi lưu
    }

    // Cập nhật thông tin người dùng
    @GetMapping("/users/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login"; // Chưa đăng nhập -> Chuyển hướng đến trang login
        }

        if (!loggedInUser.getId().equals(id) && !loggedInUser.isAdmin()) {
            return "redirect:/users"; // Không phải chính chủ hoặc admin -> Từ chối truy cập
        }

        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "user/user-form"; // Trả về trang chỉnh sửa
        }
        return "redirect:/users"; // Không tìm thấy người dùng -> Chuyển hướng
    }


    @PostMapping("/users/{id}/edit")
    public String updateuser(@PathVariable Long id, @Valid @ModelAttribute User user, BindingResult result,
    		@RequestParam("avatarFile") MultipartFile avatarFile, @RequestParam("cccdFile") MultipartFile cccdFile,Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login"; // Chưa đăng nhập
        }

        if (!loggedInUser.getId().equals(id) && !loggedInUser.isAdmin()) {
            return "redirect:/users"; // Không có quyền chỉnh sửa
        }

        if (result.hasErrors()) {
            return "user/user-form"; // Trả về lại trang chỉnh sửa nếu có lỗi
        }

        user.setId(id);
        try {
        	String avatarFileUrl = fileHelper.uploadFile(avatarFile);
        	if(avatarFileUrl != null) {
        		user.setAvatarUrl(avatarFileUrl); 
        	}
        	
        	String cccd = fileHelper.uploadFile(cccdFile);
        	if(cccd != null) {
        		user.setCccd(cccd);
        	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("upload avatar error " + e.getMessage());
		}
        userService.updateCurrentUser(user);
        return "redirect:/users/{id}"; // Chuyển hướng sau khi cập nhật
    }


    // Xóa người dùng theo ID
    @GetMapping("/users/{id}/delete")
    public String deleteuser(@PathVariable Long id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login"; // Chưa đăng nhập
        }

        if (!loggedInUser.getId().equals(id) && !loggedInUser.isAdmin()) {
            return "redirect:/users"; // Không có quyền xóa
        }

        userService.deleteById(id);
        return "redirect:/users"; // Chuyển hướng sau khi xóa
    }
    
    
    //verify cccd
    @GetMapping("/manager-user/verify")
    public String manageVerify(Model model) {
    	 List<User> users = userService.getUnapprovedUsers();     
         model.addAttribute("users", users);
         return "user/verify-list";
    }
    
    
    @GetMapping("/manager-user/verify/{id}/approve")
	public String approveRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {    	
		boolean approved = userService.approveuser(id);
		if (approved) {
			redirectAttributes.addFlashAttribute("message", "User approved successfully.");
		} else {
			redirectAttributes.addFlashAttribute("error", "Failed to approve User.");
		}
		return "redirect:/manager-user/verify";
	}

	@GetMapping("/manager-user/verify/{id}/reject")
	public String rejectRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		boolean rejected = userService.rejectuser(id);
		if (rejected) {
			redirectAttributes.addFlashAttribute("message", "User rejected successfully.");
		} else {
			redirectAttributes.addFlashAttribute("error", "Failed to reject User.");
		}
		return "redirect:/manager-user/verify";
	}

}
