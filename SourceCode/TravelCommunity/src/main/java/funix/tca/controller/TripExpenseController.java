package funix.tca.controller;

import funix.tca.entity.TripExpense;
import funix.tca.service.AppUserService;
import funix.tca.service.TripExpenseService;
import funix.tca.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/trip-expenses")
public class TripExpenseController {

    @Autowired
    private TripExpenseService tripExpenseService;

    @Autowired
    private TripService tripService;

    @Autowired
    private AppUserService appUserService;

    // Lấy tất cả các khoản chi phí
    @GetMapping("/")
    public String listTripExpenses(Model model) {
        List<TripExpense> expenses = tripExpenseService.findAll();
        model.addAttribute("expenses", expenses);
        return "trip-expense/list"; // Trang danh sách các khoản chi phí
    }

    // Lấy các khoản chi phí theo chuyến đi
    @GetMapping("/trip/{tripId}")
    public String listTripExpensesByTrip(@PathVariable Long tripId, Model model) {
        List<TripExpense> expenses = tripExpenseService.findByTripId(tripId);
        model.addAttribute("expenses", expenses);
        return "trip-expense/list"; // Trang các khoản chi phí của chuyến đi
    }

    // Tạo mới một khoản chi phí
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        TripExpense tripExpense = new TripExpense();
        model.addAttribute("tripExpense", tripExpense);
        model.addAttribute("trips", tripService.findAll()); // Cung cấp danh sách chuyến đi
        model.addAttribute("users", appUserService.findAll()); // Cung cấp danh sách người dùng
        return "trip-expense/form"; // Trang tạo mới khoản chi phí
    }

    @PostMapping("/new")
    public String createTripExpense(@Valid @ModelAttribute TripExpense tripExpense, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("trips", tripService.findAll());
            model.addAttribute("users", appUserService.findAll());
            return "trip-expense/form"; // Trả lại trang nếu có lỗi
        }

        // Lưu khoản chi phí
        tripExpenseService.save(tripExpense);
        return "redirect:/trip-expenses/"; // Chuyển hướng về trang danh sách
    }

    // Xóa một khoản chi phí
    @GetMapping("/{id}/delete")
    public String deleteTripExpense(@PathVariable Long id) {
        tripExpenseService.deleteById(id);
        return "redirect:/trip-expenses/"; // Chuyển hướng về trang danh sách
    }
}
