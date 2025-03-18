package funix.tgcp.payment;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // API tạo payment
    @PostMapping("/create")
    public ResponseEntity<Payment> createPayment(
            @RequestParam Long bookingId,
            @RequestParam double amount,
            @RequestParam String senderAccountNumber,
            @RequestParam String senderAccountName,
            @RequestParam String transactionNote,
            @RequestParam("file") MultipartFile file
    ) {
        try {
			return ResponseEntity.ok(paymentService.createPayment(
			        bookingId, amount, senderAccountNumber, senderAccountName, transactionNote, file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

    // API guide xác nhận payment
    @PutMapping("/{paymentId}/confirm")
    public ResponseEntity<Payment> confirmPayment(
            @PathVariable Long paymentId
    ) {
        return ResponseEntity.ok(paymentService.confirmPayment(paymentId));
    }
}

