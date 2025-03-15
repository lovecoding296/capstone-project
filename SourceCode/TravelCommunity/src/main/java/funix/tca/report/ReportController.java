package funix.tca.report;

import funix.tca.appuser.AppUser;
import funix.tca.appuser.AppUserService;
import funix.tca.post.PostService;
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
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private PostService postService;

    @Autowired
    private TripService tripService;

    // Lấy tất cả báo cáo
    @GetMapping("/")
    public String listReports(Model model, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !loggedInUser.isAdmin()) {
            return "redirect:/login"; // Chỉ admin mới có quyền xem danh sách báo cáo
        }

        List<Report> reports = reportService.findAll();
        model.addAttribute("reports", reports);
        return "report/list"; // Trả về trang danh sách báo cáo
    }

    // Lấy các báo cáo chưa giải quyết
    @GetMapping("/pending")
    public String listPendingReports(Model model, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !loggedInUser.isAdmin()) {
            return "redirect:/login";
        }

        List<Report> pendingReports = reportService.findByResolved(false);
        model.addAttribute("reports", pendingReports);
        return "report/list";
    }

    // Tạo mới báo cáo
    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Yêu cầu đăng nhập để tạo báo cáo
        }

        model.addAttribute("report", new Report());
        model.addAttribute("users", appUserService.findAll());
        model.addAttribute("posts", postService.findAll());
        model.addAttribute("trips", tripService.findAll());
        return "report/form";
    }

    @PostMapping("/new")
    public String createReport(@Valid @ModelAttribute Report report, BindingResult result, Model model, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("users", appUserService.findAll());
            model.addAttribute("posts", postService.findAll());
            model.addAttribute("trips", tripService.findAll());
            return "report/form";
        }

        report.setReportedBy(loggedInUser);
        reportService.save(report);
        return "redirect:/reports/";
    }

    // Xử lý báo cáo
    @GetMapping("/{id}/resolve")
    public String resolveReport(@PathVariable Long id, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !loggedInUser.isAdmin()) {
            return "redirect:/login"; // Chỉ admin mới có quyền xử lý báo cáo
        }

        Report report = reportService.findById(id).orElseThrow(() -> new RuntimeException("Report not found"));
        report.setResolved(true);
        reportService.update(report);
        return "redirect:/reports/pending";
    }

    // Xóa báo cáo
    @GetMapping("/{id}/delete")
    public String deleteReport(@PathVariable Long id, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !loggedInUser.isAdmin()) {
            return "redirect:/login";
        }

        reportService.deleteById(id);
        return "redirect:/reports/";
    }
}
