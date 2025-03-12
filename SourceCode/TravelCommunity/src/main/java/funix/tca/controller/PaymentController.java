package funix.tca.controller;

import funix.tca.entity.Payment;
import funix.tca.entity.Trip;
import funix.tca.service.PaymentService;
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
    public String listPayments(Model model) {
        List<Payment> payments = paymentService.findAll();
        model.addAttribute("payments", payments);
        return "payment/list"; // Trả về trang danh sách giao dịch thanh toán
    }

    // Lấy các giao dịch thanh toán đã xác nhận
    @GetMapping("/confirmed")
    public String listConfirmedPayments(Model model) {
        List<Payment> confirmedPayments = paymentService.findByConfirmed(true);
        model.addAttribute("payments", confirmedPayments);
        return "payment/list"; // Trả về trang giao dịch đã xác nhận
    }

    // Lấy các giao dịch thanh toán của một chuyến đi
    @GetMapping("/trip/{tripId}")
    public String listPaymentsByTrip(@PathVariable Long tripId, Model model) {
        List<Payment> payments = paymentService.findByTripId(tripId);
        model.addAttribute("payments", payments);
        return "payment/list"; // Trả về trang giao dịch của chuyến đi
    }

    // Tạo mới giao dịch thanh toán
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Payment payment = new Payment();
        model.addAttribute("payment", payment);
        model.addAttribute("users", appUserService.findAll()); // Cung cấp danh sách người dùng
        model.addAttribute("trips", tripService.findAll()); // Cung cấp danh sách chuyến đi
        return "payment/form"; // Trả về trang tạo giao dịch thanh toán
    }

    @PostMapping("/new")
    public String createPayment(@Valid @ModelAttribute Payment payment, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", appUserService.findAll());
            model.addAttribute("trips", tripService.findAll());
            return "payment/form"; // Trả về lại trang tạo giao dịch thanh toán nếu có lỗi
        }

        // Lưu giao dịch thanh toán
        paymentService.save(payment);
        return "redirect:/payments/"; // Chuyển hướng về trang danh sách giao dịch thanh toán
    }

    // Xác nhận giao dịch thanh toán
    @GetMapping("/{id}/confirm")
    public String confirmPayment(@PathVariable Long id) {
        Payment payment = paymentService.findById(id).orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setConfirmed(true);
        paymentService.update(payment);
        return "redirect:/payments/confirmed"; // Chuyển hướng về trang giao dịch thanh toán đã xác nhận
    }

    // Xóa giao dịch thanh toán
    @GetMapping("/{id}/delete")
    public String deletePayment(@PathVariable Long id) {
        paymentService.deleteById(id);
        return "redirect:/payments/"; // Chuyển hướng về trang danh sách giao dịch thanh toán
    }
}
