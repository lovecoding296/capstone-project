package funix.tgcp.user;


import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import funix.tgcp.booking.BookingService;
import funix.tgcp.booking.review.Review;
import funix.tgcp.booking.review.ReviewService;
import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.guide.service.GroupSizeCategory;
import funix.tgcp.guide.service.GuideService;
import funix.tgcp.guide.service.ServiceType;
import funix.tgcp.post.Post;
import funix.tgcp.post.PostService;
import funix.tgcp.util.FileHelper;
import jakarta.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
	
	@Autowired
	private FileHelper fileHelper;

	@Autowired
	private PostService postService;

	@Autowired
	private BookingService bookingService;

	@Autowired
	private ReviewService reviewService;
    
	// Lấy thông tin người dùng theo ID
	@GetMapping("/users/{userId}")
	public String getuserById(@PathVariable Long userId, Model model) {
		try {
			User user = userService.getUserDetails(userId);
            List<Post> posts = postService.findTop3ByAuthorIdOrderByCreatedAtDesc(userId);            
            List<Review> reviews = reviewService.findByReviewedUserIdOrderByRatingDesc(userId);
        	long bookingCount = bookingService.countCompletedByUserIdOrGuideId(userId);
        	
        	
            model.addAttribute("posts", posts);
            model.addAttribute("reviews", reviews);
            model.addAttribute("bookingCount", bookingCount);	
			
			model.addAttribute("user", user);
			
			
			
			
			return "user/user-details";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/"; // Nếu không tìm thấy, chuyển hướng về trang danh sách
	}

    // Cập nhật thông tin người dùng
    @GetMapping("/users/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
    	if (userDetails == null) {
			return "redirect:/login";
		}

		User loggedInUser = userDetails.getUser();

        if (!loggedInUser.getId().equals(id) && !loggedInUser.isAdmin()) {
            return "redirect:/"; // Không phải chính chủ hoặc admin -> Từ chối truy cập
        }

        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "user/user-form"; // Trả về trang chỉnh sửa
        }
        return "redirect:/"; // Không tìm thấy người dùng -> Chuyển hướng
    }


	@PostMapping("/users/{id}/edit")
	public String updateUser(@PathVariable Long id, @Valid @ModelAttribute User user, BindingResult result,
			@RequestParam MultipartFile avatarFile, @RequestParam MultipartFile cccdFile, Model model,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		if (userDetails == null) {
			return "redirect:/login"; // Chưa đăng nhập
		}

		User loggedInUser = userDetails.getUser();

		if (!loggedInUser.getId().equals(id) && !loggedInUser.isAdmin()) {
			return "redirect:/"; // Không có quyền chỉnh sửa
		}

		if (result.hasErrors()) {
			return "user/user-form"; // Trả về lại trang chỉnh sửa nếu có lỗi
		}

		user.setId(id);
		try {
			String avatarFileUrl = fileHelper.uploadFile(avatarFile);
			if (avatarFileUrl != null) {
				fileHelper.deleteFile(user.getAvatarUrl());
				user.setAvatarUrl(avatarFileUrl);
			}

			String cccd = fileHelper.uploadFile(cccdFile);
			if (cccd != null) {
				user.setCccd(cccd);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("upload avatar error " + e.getMessage());
		}
		userService.updateCurrentUser(user);
		return "redirect:/users/{id}"; // Chuyển hướng sau khi cập nhật
	}


	// Xóa người dùng theo ID
	@GetMapping("/users/{id}/delete")
	public String deleteUser(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
		if (userDetails == null) {
			return "redirect:/login"; // Chưa đăng nhập
		}

		User loggedInUser = userDetails.getUser();

		if (!loggedInUser.getId().equals(id) && !loggedInUser.isAdmin()) {
			return "redirect:/";
		}

		userService.deleteById(id);
		return "redirect:/";
	}
    
    @GetMapping("/guides")
    public String searchGuides(
            @RequestParam(required = false) ServiceType serviceType,
            @RequestParam(required = false) City city,
            @RequestParam(required = false) Language language,
            @RequestParam(required = false) GroupSizeCategory groupSize,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) Boolean isLocalGuide,
            @RequestParam(required = false) Boolean isInternationalGuide,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model) {

    	Pageable pageable = PageRequest.of(page, size); // Tạo Pageable    	
    	Page<User> userPage = userService.searchGuides(serviceType, city, language, groupSize, gender, isLocalGuide, isInternationalGuide, pageable);

		for (User u : userPage.getContent()) {
			Set<City> cities = new HashSet<>();
			Set<ServiceType> types = new HashSet<>();
			Set<Language> languages = new HashSet<>();
			Set<GroupSizeCategory> groupSizes = new HashSet<>();

			for (GuideService g : u.getGuideServices()) {
				cities.add(g.getCity());
				types.add(g.getType());
				languages.add(g.getLanguage());
				groupSizes.add(g.getGroupSizeCategory());
			}
			u.setCities(cities);
			u.setServiceTypes(types);
			u.setLanguages(languages);
			u.setGroupSizes(groupSizes);
		}
		
        model.addAttribute("users", userPage.getContent()); // Lấy nội dung trang
        model.addAttribute("totalPages", userPage.getTotalPages()); // Số trang
        model.addAttribute("currentPage", page); // Trang hiện tại
        
        model.addAttribute("cities", City.values());
        model.addAttribute("languages", Language.values());
        model.addAttribute("serviceTypes", ServiceType.values());
        model.addAttribute("groupSizes", GroupSizeCategory.values());
        

        return "guide/guide-list"; // Tên view Thymeleaf trả về, ví dụ: templates/guides/list.html
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
		boolean approved = userService.approveUser(id);
		if (approved) {
			redirectAttributes.addFlashAttribute("message", "User approved successfully.");
		} else {
			redirectAttributes.addFlashAttribute("error", "Failed to approve User.");
		}
		return "redirect:/manager-user/verify";
	}

	@GetMapping("/manager-user/verify/{id}/reject")
	public String rejectRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		boolean rejected = userService.rejectUser(id);
		if (rejected) {
			redirectAttributes.addFlashAttribute("message", "User rejected successfully.");
		} else {
			redirectAttributes.addFlashAttribute("error", "Failed to reject User.");
		}
		return "redirect:/manager-user/verify";
	}

}
