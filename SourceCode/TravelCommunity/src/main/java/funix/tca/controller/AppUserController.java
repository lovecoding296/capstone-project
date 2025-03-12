package funix.tca.controller;


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

import funix.tca.entity.AppUser;
import funix.tca.service.AppUserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/appusers")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    // Lấy danh sách tất cả người dùng
    @GetMapping("/")
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
            model.addAttribute("appUser", appUser.get());
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
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<AppUser> appUser = appUserService.findById(id);
        if (appUser.isPresent()) {
            model.addAttribute("appUser", appUser.get());
            return "appuser/form"; // Trả về trang chỉnh sửa người dùng
        }
        return "redirect:/appusers"; // Nếu không tìm thấy, chuyển hướng về trang danh sách
    }

    @PostMapping("/{id}/edit")
    public String updateAppUser(@PathVariable Long id, @Valid @ModelAttribute AppUser appUser, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "appuser/form"; // Trả về lại trang chỉnh sửa nếu có lỗi
        }
        appUser.setId(id);
        appUserService.update(appUser);
        return "redirect:/appusers"; // Chuyển hướng đến danh sách người dùng sau khi cập nhật
    }

    // Xóa người dùng theo ID
    @GetMapping("/{id}/delete")
    public String deleteAppUser(@PathVariable Long id) {
        appUserService.deleteById(id);
        return "redirect:/appusers"; // Chuyển hướng về trang danh sách sau khi xóa
    }
}
