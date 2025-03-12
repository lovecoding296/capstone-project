package funix.tca.controller;

import funix.tca.entity.Report;
import funix.tca.service.ReportService;
import funix.tca.service.AppUserService;
import funix.tca.service.PostService;
import funix.tca.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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
    public String listReports(Model model) {
        List<Report> reports = reportService.findAll();
        model.addAttribute("reports", reports);
        return "report/list"; // Trả về trang danh sách báo cáo
    }

    // Lấy các báo cáo chưa giải quyết
    @GetMapping("/pending")
    public String listPendingReports(Model model) {
        List<Report> pendingReports = reportService.findByResolved(false);
        model.addAttribute("reports", pendingReports);
        return "report/list"; // Trả về trang danh sách báo cáo chưa giải quyết
    }

    // Tạo mới báo cáo
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Report report = new Report();
        model.addAttribute("report", report);
        model.addAttribute("users", appUserService.findAll()); // Cung cấp danh sách người dùng
        model.addAttribute("posts", postService.findAll()); // Cung cấp danh sách bài viết
        model.addAttribute("trips", tripService.findAll()); // Cung cấp danh sách chuyến đi
        return "report/form"; // Trả về trang tạo báo cáo
    }

    @PostMapping("/new")
    public String createReport(@Valid @ModelAttribute Report report, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", appUserService.findAll());
            model.addAttribute("posts", postService.findAll());
            model.addAttribute("trips", tripService.findAll());
            return "report/form"; // Trả về lại trang tạo báo cáo nếu có lỗi
        }

        // Thiết lập người dùng báo cáo và báo cáo đối tượng
        reportService.save(report);
        return "redirect:/reports/"; // Chuyển hướng về trang danh sách báo cáo
    }

    // Xử lý báo cáo
    @GetMapping("/{id}/resolve")
    public String resolveReport(@PathVariable Long id) {
        Report report = reportService.findById(id).orElseThrow(() -> new RuntimeException("Report not found"));
        report.setResolved(true);
        reportService.update(report);
        return "redirect:/reports/pending"; // Chuyển hướng về trang báo cáo chưa giải quyết
    }

    // Xóa báo cáo
    @GetMapping("/{id}/delete")
    public String deleteReport(@PathVariable Long id) {
        reportService.deleteById(id);
        return "redirect:/reports/"; // Chuyển hướng về trang danh sách báo cáo
    }
}
