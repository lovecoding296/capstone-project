package funix.tca.controller;

import funix.tca.entity.Trip;
import funix.tca.service.AppUserService;
import funix.tca.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

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

    // Lấy các chuyến đi của người tổ chức
    @GetMapping("/creator/{creatorId}")
    public String listTripsByCreator(@PathVariable Long creatorId, Model model) {
        List<Trip> trips = tripService.findByCreatorId(creatorId);
        model.addAttribute("trips", trips);
        return "trip/list"; // Trang các chuyến đi của người tổ chức
    }

    // Tạo mới một chuyến đi
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Trip trip = new Trip();
        model.addAttribute("trip", trip);
        model.addAttribute("users", appUserService.findAll()); // Cung cấp danh sách người dùng
        return "trip/form"; // Trang tạo mới chuyến đi
    }

    @PostMapping("/new")
    public String createTrip(@Valid @ModelAttribute Trip trip, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", appUserService.findAll());
            return "trip/form"; // Trả lại trang nếu có lỗi
        }

        // Lưu chuyến đi
        tripService.save(trip);
        return "redirect:/trips/"; // Chuyển hướng về trang danh sách
    }

    // Cập nhật một chuyến đi
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Trip trip = tripService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid trip ID:" + id));
        model.addAttribute("trip", trip);
        model.addAttribute("users", appUserService.findAll());
        return "trip/form"; // Trang chỉnh sửa chuyến đi
    }

    @PostMapping("/{id}/edit")
    public String updateTrip(@PathVariable Long id, @Valid @ModelAttribute Trip trip, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", appUserService.findAll());
            return "trip/form"; // Trả lại trang nếu có lỗi
        }

        // Cập nhật chuyến đi
        trip.setId(id);
        tripService.save(trip);
        return "redirect:/trips/"; // Chuyển hướng về trang danh sách
    }

    // Xóa một chuyến đi
    @GetMapping("/{id}/delete")
    public String deleteTrip(@PathVariable Long id) {
        tripService.deleteById(id);
        return "redirect:/trips/"; // Chuyển hướng về trang danh sách
    }
}
