package funix.tca.trip.expense;

import funix.tca.appuser.AppUser;
import funix.tca.appuser.AppUserService;
import funix.tca.trip.Trip;
import funix.tca.trip.TripService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    public String showCreateForm(Model model, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
        }

        model.addAttribute("tripExpense", new TripExpense());
        model.addAttribute("trips", tripService.findAll());
        model.addAttribute("users", appUserService.findAll());
        return "trip-expense/form"; // Trang tạo mới khoản chi phí
    }

    @PostMapping("/new")
    public String createTripExpense(@Valid @ModelAttribute TripExpense tripExpense, BindingResult result, Model model, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
        }

        if (result.hasErrors()) {
            model.addAttribute("trips", tripService.findAll());
            model.addAttribute("users", appUserService.findAll());
            return "trip-expense/form";
        }

        tripExpense.setPaidBy(loggedInUser); // Gán người tạo chi phí là loggedInUser
        tripExpenseService.save(tripExpense);
        return "redirect:/trip-expenses/";
    }

    // Xóa một khoản chi phí
    @GetMapping("/{id}/delete")
    public String deleteTripExpense(@PathVariable Long id, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
        }

        TripExpense expense = tripExpenseService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid expense ID: " + id));
        Trip trip = expense.getTrip();

        // Kiểm tra quyền xóa (chỉ người tạo chuyến đi hoặc người ghi nhận chi phí mới có quyền)
        if (!trip.getCreator().getId().equals(loggedInUser.getId()) && !expense.getPaidBy().getId().equals(loggedInUser.getId())) {
            return "redirect:/trip-expenses/?error=unauthorized";
        }

        tripExpenseService.deleteById(id);
        return "redirect:/trip-expenses/";
    }
}
