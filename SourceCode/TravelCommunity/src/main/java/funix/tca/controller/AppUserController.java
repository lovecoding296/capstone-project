package funix.tca.controller;


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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import funix.tca.entity.AppUser;
import funix.tca.entity.Trip;
import funix.tca.entity.Post;
import funix.tca.service.AppUserService;
import funix.tca.service.PostService;
import funix.tca.service.TripService;
import funix.tca.util.FileUploadHelper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/appusers")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private TripService tripService;
    
    @Autowired
    private PostService postService;
    
    // Lấy danh sách tất cả người dùng
    @GetMapping
    public String getAllAppUsers(Model model) {
        List<AppUser> appUsers = appUserService.findAll();
        
        
        model.addAttribute("appUsers", appUsers);
        return "appuser/list"; // Trả về trang Thymeleaf với danh sách người dùng
    }

    // Lấy thông tin người dùng theo ID
    @GetMapping("/{id}")
    public String getAppUserById(@PathVariable Long id, Model model) {
        Optional<AppUser> appUser = appUserService.findById(id);
        if (appUser.isPresent()) {
        	
        	List<Trip> trips = tripService.findByCreatorId(appUser.get().getId());
            List<Post> posts = postService.findByAuthorId(appUser.get().getId());
        	
            model.addAttribute("appUser", appUser.get());
            model.addAttribute("trips", trips);
            model.addAttribute("posts", posts);
            return "appuser/detail"; // Trả về trang chi tiết người dùng
        }
        return "redirect:/appusers"; // Nếu không tìm thấy, chuyển hướng về trang danh sách
    }

    // Tạo mới người dùng
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("appUser", new AppUser());
        return "appuser/form"; // Trả về trang Thymeleaf để tạo người dùng mới
    }

    @PostMapping("/new")
    public String createAppUser(@Valid @ModelAttribute AppUser appUser, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "appuser/form"; // Trả về lại trang tạo nếu có lỗi
        }
        appUserService.save(appUser);
        return "redirect:/appusers"; // Chuyển hướng đến danh sách người dùng sau khi lưu
    }

    // Cập nhật thông tin người dùng
    @GetMapping("/{id}/edit")
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
            return "appuser/form"; // Trả về trang chỉnh sửa
        }
        return "redirect:/appusers"; // Không tìm thấy người dùng -> Chuyển hướng
    }


    @PostMapping("/{id}/edit")
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
            return "appuser/form"; // Trả về lại trang chỉnh sửa nếu có lỗi
        }

        appUser.setId(id);
        try {
        	String avatarFileUrl = FileUploadHelper.uploadFile(avatarFile,"appusers/", appUser.getId().toString());
        	if(avatarFileUrl != null) {
        		appUser.setAvatarUrl(avatarFileUrl); 
        	}
        	
        	String cccd = FileUploadHelper.uploadFile(cccdFile,"appusers/cccd/", appUser.getId().toString());
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
    @GetMapping("/{id}/delete")
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

}
