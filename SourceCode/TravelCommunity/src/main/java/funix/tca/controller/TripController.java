package funix.tca.controller;

import funix.tca.entity.AppUser;
import funix.tca.entity.Trip;
import funix.tca.service.AppUserService;
import funix.tca.service.TripService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    @Autowired
    private AppUserService appUserService;

    // Lấy tất cả các chuyến đi
    @GetMapping("/")
    public String listTrips(Model model) {
        List<Trip> trips = tripService.findAll();
        model.addAttribute("trips", trips);
        return "trip/list"; // Trang danh sách các chuyến đi
    }
    
    
    @GetMapping("/{id}/details")
    public String tripById(@PathVariable Long id, Model model) {
        Optional<Trip> tripOptional = tripService.findById(id);
        if (tripOptional.isPresent()) {
            Trip trip = tripOptional.get();
            model.addAttribute("trip", trip);
            return "trip/trip-detail";
        }
        return "error";
    }
    
    @PostMapping("/{id}/join")
    public String joinTrip(@PathVariable Long id, HttpSession session) {
    	
    	AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
    	if (loggedInUser == null) {
            return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
        }
        Optional<Trip> tripOptional = tripService.findById(id);
        if (tripOptional.isPresent()) {
            Trip trip = tripOptional.get();

            // Kiểm tra xem người dùng đã tham gia chưa
            if (!trip.getParticipants().contains(loggedInUser)) {
            	System.out.println("Bạn đã tham gia thành công");
                trip.getParticipants().add(loggedInUser);
                tripService.save(trip);
            } else {
            	System.out.println("Bạn đã tham gia rồi");
            }
        }
        return "redirect:/trips/" + id + "/details";
    }



    // Lấy các chuyến đi của người tổ chức
    @GetMapping("/creator/{creatorId}")
    public String listTripsByCreator(@PathVariable Long creatorId, Model model) {
        List<Trip> trips = tripService.findByCreatorId(creatorId);
        model.addAttribute("trips", trips);
        return "trip/list"; // Trang các chuyến đi của người tổ chức
    }

    // Tạo mới một chuyến đi
    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
        }

        model.addAttribute("trip", new Trip());
        model.addAttribute("users", appUserService.findAll()); 
        return "trip/form"; // Trang tạo mới chuyến đi
    }

    @PostMapping("/new")
    public String createTrip(@Valid @ModelAttribute Trip trip, BindingResult result, Model model, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
        }

        if (result.hasErrors()) {
            model.addAttribute("users", appUserService.findAll());
            return "trip/form"; // Trả lại trang nếu có lỗi
        }

        trip.setCreator(user);
        tripService.save(trip);
        return "redirect:/trips/"; // Chuyển hướng về trang danh sách
    }

    // Cập nhật một chuyến đi
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
        }

        Trip trip = tripService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid trip ID: " + id));

        // Kiểm tra quyền chỉnh sửa
        if (!trip.getCreator().getId().equals(user.getId())) {
            return "redirect:/trips/?error=unauthorized"; // Không cho phép chỉnh sửa nếu không phải chủ sở hữu
        }

        model.addAttribute("trip", trip);
        model.addAttribute("users", appUserService.findAll());
        return "trip/form"; // Trang chỉnh sửa chuyến đi
    }

    @PostMapping("/{id}/edit")
    public String updateTrip(@PathVariable Long id, @Valid @ModelAttribute Trip trip, BindingResult result, Model model, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
        }

        // Kiểm tra quyền chỉnh sửa
        Trip existingTrip = tripService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid trip ID: " + id));
        if (!existingTrip.getCreator().getId().equals(user.getId())) {
            return "redirect:/trips/?error=unauthorized";
        }

        if (result.hasErrors()) {
            model.addAttribute("users", appUserService.findAll());
            return "trip/form";
        }

        trip.setId(id);
        trip.setCreator(existingTrip.getCreator()); // Giữ nguyên creator
        tripService.save(trip);
        return "redirect:/trips/";
    }

    // Xóa một chuyến đi
    @GetMapping("/{id}/delete")
    public String deleteTrip(@PathVariable Long id, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
        }

        Trip trip = tripService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid trip ID: " + id));

        // Kiểm tra quyền xóa
        if (!trip.getCreator().getId().equals(user.getId())) {
            return "redirect:/trips/?error=unauthorized";
        }

        tripService.deleteById(id);
        return "redirect:/trips/";
    }
}
