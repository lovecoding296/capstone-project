package funix.tca.controller;

import funix.tca.entity.Payment;
import funix.tca.entity.Trip;
import funix.tca.entity.AppUser;
import funix.tca.service.PaymentService;
import funix.tca.service.AppUserService;
import funix.tca.service.TripService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private TripService tripService;

    // Lấy tất cả giao dịch thanh toán
    @GetMapping("/")
    public String listPayments(Model model, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
        }

        List<Payment> payments = paymentService.findAll();
        model.addAttribute("payments", payments);
        return "payment/list";
    }

    // Tạo mới giao dịch thanh toán
    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Kiểm tra đăng nhập
        }

        Payment payment = new Payment();
        model.addAttribute("payment", payment);
        model.addAttribute("users", appUserService.findAll());
        model.addAttribute("trips", tripService.findAll());
        return "payment/form";
    }

    @PostMapping("/new")
    public String createPayment(@Valid @ModelAttribute Payment payment, BindingResult result, Model model, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Kiểm tra đăng nhập
        }

        if (result.hasErrors()) {
            model.addAttribute("users", appUserService.findAll());
            model.addAttribute("trips", tripService.findAll());
            return "payment/form";
        }
        payment.setPayer(loggedInUser);
        paymentService.save(payment);
        return "redirect:/payments/";
    }

    // Xác nhận giao dịch thanh toán
    @GetMapping("/{id}/confirm")
    public String confirmPayment(@PathVariable Long id, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Kiểm tra đăng nhập
        }        
        
        Payment payment = paymentService.findById(id).orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if(loggedInUser.getId() == payment.getPayer().getId()) {
        	payment.setConfirmed(true);
            paymentService.update(payment);
            return "redirect:/payments/confirmed";
        }
        return "error";
        
    }

    // Xóa giao dịch thanh toán
    @GetMapping("/{id}/delete")
    public String deletePayment(@PathVariable Long id, HttpSession session) {
        AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Kiểm tra đăng nhập
        }
    	paymentService.deleteById(id);
        return "redirect:/payments/";
        
    }
}
