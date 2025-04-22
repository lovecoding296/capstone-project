package funix.tgcp.guide;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import funix.tgcp.user.City;
import funix.tgcp.user.Gender;
import funix.tgcp.user.Language;
import funix.tgcp.user.User;
import funix.tgcp.user.UserService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.List;

@Controller
@RequestMapping("/guides")
public class GuideController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String searchGuides(
            @RequestParam(required = false) City city,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) Language language,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model) {

    	Pageable pageable = PageRequest.of(page, size); // Tạo Pageable    	
    	Page<User> userPage = userService.searchGuides(city, maxPrice, gender, language, pageable);

        model.addAttribute("users", userPage.getContent()); // Lấy nội dung trang
        model.addAttribute("totalPages", userPage.getTotalPages()); // Số trang
        model.addAttribute("currentPage", page); // Trang hiện tại
        
        model.addAttribute("cities", City.values());
        model.addAttribute("languages", Language.values());

        return "guide/guide-list"; // Tên view Thymeleaf trả về, ví dụ: templates/guides/list.html
    }
}
