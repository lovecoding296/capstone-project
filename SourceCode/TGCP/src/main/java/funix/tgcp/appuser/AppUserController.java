package funix.tgcp.appuser;


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

import funix.tgcp.exception.EmailVerificationException;
import funix.tgcp.post.Post;
import funix.tgcp.post.PostService;
import funix.tgcp.review.Review;
import funix.tgcp.review.ReviewService;
import funix.tgcp.trip.Trip;
import funix.tgcp.trip.TripService;
import funix.tgcp.util.FileUploadHelper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private TripService tripService;
    
    @Autowired
    private PostService postService;
    
	@Autowired
    private ReviewService reviewService;
	
	@Autowired
	private FileUploadHelper fileUploadHelper;
    
    
    
    // Lấy danh sách tất cả người dùng
    @GetMapping("/appusers")
    public String getAllAppUsers(Model model) {
        List<AppUser> appUsers = appUserService.findAll();
        
        
        model.addAttribute("appUsers", appUsers);
        return "appuser/appuser-list"; // Trả về trang Thymeleaf với danh sách người dùng
    }

    // Lấy thông tin người dùng theo ID
    @GetMapping("/appusers/{id}")
    public String getAppUserById(@PathVariable Long id, Model model) {
        Optional<AppUser> appUser = appUserService.findById(id);
        if (appUser.isPresent()) {
        	
        	List<Trip> trips = tripService.findTop3ByCreatorId(appUser.get().getId());
            List<Post> posts = postService.findTop3ByAuthorIdOrderByCreatedAtDesc(appUser.get().getId());            
            List<Review> reviews = reviewService.findTop3ByReviewedUserIdOrderByReviewDateDesc(appUser.get().getId());

        	
            model.addAttribute("appUser", appUser.get());
            model.addAttribute("trips", trips);
            model.addAttribute("posts", posts);
            model.addAttribute("reviews", reviews);
            return "appuser/appuser-details"; // Trả về trang chi tiết người dùng
        }
        return "redirect:/appusers"; // Nếu không tìm thấy, chuyển hướng về trang danh sách
    }

    // Tạo mới người dùng
    @GetMapping("/appusers/new")
    public String showCreateForm(Model model) {
        model.addAttribute("appUser", new AppUser());
        return "appuser/form"; // Trả về trang Thymeleaf để tạo người dùng mới
    }

    @PostMapping("/appusers/new")
    public String createAppUser(@Valid @ModelAttribute AppUser appUser, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "appuser/appuser-form"; // Trả về lại trang tạo nếu có lỗi
        }
        appUserService.save(appUser);
        return "redirect:/appusers"; // Chuyển hướng đến danh sách người dùng sau khi lưu
    }

    // Cập nhật thông tin người dùng
    @GetMapping("/appusers/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login"; // Chưa đăng nhập -> Chuyển hướng đến trang login
        }

        if (!loggedInUser.getId().equals(id) && !loggedInUser.isAdmin()) {
            return "redirect:/appusers"; // Không phải chính chủ hoặc admin -> Từ chối truy cập
        }

        Optional<AppUser> appUser = appUserService.findById(id);
        if (appUser.isPresent()) {
            model.addAttribute("appUser", appUser.get());
            return "appuser/appuser-form"; // Trả về trang chỉnh sửa
        }
        return "redirect:/appusers"; // Không tìm thấy người dùng -> Chuyển hướng
    }


    @PostMapping("/appusers/{id}/edit")
    public String updateAppUser(@PathVariable Long id, @Valid @ModelAttribute AppUser appUser, BindingResult result,
    		@RequestParam("avatarFile") MultipartFile avatarFile, @RequestParam("cccdFile") MultipartFile cccdFile,Model model, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login"; // Chưa đăng nhập
        }

        if (!loggedInUser.getId().equals(id) && !loggedInUser.isAdmin()) {
            return "redirect:/appusers"; // Không có quyền chỉnh sửa
        }

        if (result.hasErrors()) {
            return "appuser/appuser-form"; // Trả về lại trang chỉnh sửa nếu có lỗi
        }

        appUser.setId(id);
        try {
        	String avatarFileUrl = fileUploadHelper.uploadFile(avatarFile);
        	if(avatarFileUrl != null) {
        		appUser.setAvatarUrl(avatarFileUrl); 
        	}
        	
        	String cccd = fileUploadHelper.uploadFile(cccdFile);
        	if(cccd != null) {
        		appUser.setCccd(cccd);
        	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("upload avatar error " + e.getMessage());
		}
        appUserService.updateCurrentUser(appUser);
        return "redirect:/appusers/{id}"; // Chuyển hướng sau khi cập nhật
    }


    // Xóa người dùng theo ID
    @GetMapping("/appusers/{id}/delete")
    public String deleteAppUser(@PathVariable Long id, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login"; // Chưa đăng nhập
        }

        if (!loggedInUser.getId().equals(id) && !loggedInUser.isAdmin()) {
            return "redirect:/appusers"; // Không có quyền xóa
        }

        appUserService.deleteById(id);
        return "redirect:/appusers"; // Chuyển hướng sau khi xóa
    }
    
    
    //verify cccd
    @GetMapping("/manager-appuser/verify")
    public String manageVerify(Model model) {
    	 List<AppUser> appUsers = appUserService.getUnapprovedUsers();     
         model.addAttribute("appUsers", appUsers);
         return "appuser/verify-list";
    }
    
    
    @GetMapping("/manager-appuser/verify/{id}/approve")
	public String approveRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {    	
		boolean approved = appUserService.approveAppUser(id);
		if (approved) {
			redirectAttributes.addFlashAttribute("message", "User approved successfully.");
		} else {
			redirectAttributes.addFlashAttribute("error", "Failed to approve User.");
		}
		return "redirect:/manager-appuser/verify";
	}

	@GetMapping("/manager-appuser/verify/{id}/reject")
	public String rejectRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		boolean rejected = appUserService.rejectAppUser(id);
		if (rejected) {
			redirectAttributes.addFlashAttribute("message", "User rejected successfully.");
		} else {
			redirectAttributes.addFlashAttribute("error", "Failed to reject User.");
		}
		return "redirect:/manager-appuser/verify";
	}

}
