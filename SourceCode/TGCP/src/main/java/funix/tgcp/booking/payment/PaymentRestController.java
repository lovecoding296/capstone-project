package funix.tgcp.booking.payment;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.util.FileHelper;
import funix.tgcp.util.LogHelper;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentRestController {

	private static final LogHelper logger = new LogHelper(PaymentRestController.class);

	private final PaymentService paymentService;

	@Autowired
	private FileHelper fileHelper;

	@DeleteMapping("/{paymentId}")
	public ResponseEntity<String> deletePayment(@PathVariable Long paymentId, @AuthenticationPrincipal CustomUserDetails auth) {
		paymentService.delete(paymentId, auth.getId());
		return ResponseEntity.ok("Deleted successfully!");
	}

	public PaymentRestController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	// API tạo payment
	@PostMapping("/create")
	public ResponseEntity<?> createPayment(@RequestParam Long bookingId,
			@Valid @RequestPart("payment") Payment paymentRequest, 
			BindingResult bindingResult,
			@RequestPart("transactionImage") MultipartFile file,
			@AuthenticationPrincipal CustomUserDetails auth)
			throws IOException {

		logger.info(" bookingId " + bookingId);

		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(Map.of("message", "Invalid payment data"));
		}
		return ResponseEntity.ok(paymentService.createPayment(bookingId, paymentRequest, file, auth.getId()));
	}

	@PutMapping("/{paymentId}")
	public ResponseEntity<?> updatePayment(@PathVariable Long paymentId, @RequestPart("payment") Payment updatedPayment,
			@RequestPart(value = "transactionImage", required = false) MultipartFile transactionImage, 
			@AuthenticationPrincipal CustomUserDetails auth) {
		try {
			Optional<Payment> existingPaymentOpt = paymentService.findById(paymentId);
			if (!existingPaymentOpt.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Payment not found"));
			}

			Payment existingPayment = existingPaymentOpt.get();

			// Cập nhật thông tin payment
			existingPayment.setAmount(updatedPayment.getAmount());
			existingPayment.setSenderAccountNumber(updatedPayment.getSenderAccountNumber());
			existingPayment.setSenderAccountName(updatedPayment.getSenderAccountName());
			existingPayment.setTransactionNote(updatedPayment.getTransactionNote());

			if (transactionImage != null && !transactionImage.isEmpty()) {
				String imageUrl = fileHelper.uploadFile(transactionImage);
				if (imageUrl != null) {
					fileHelper.deleteFile(existingPayment.getTransactionImageUrl());
					existingPayment.setTransactionImageUrl(imageUrl);
				}
			}

			Payment updated = paymentService.save(existingPayment);
			return ResponseEntity.ok(updated);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("message", "Error processing payment update"));
		}
	}

	// API guide xác nhận payment
	@PutMapping("/{paymentId}/confirm")
	public ResponseEntity<Payment> confirmPayment(@PathVariable Long paymentId, @AuthenticationPrincipal CustomUserDetails auth) {
		return ResponseEntity.ok(paymentService.confirmPayment(paymentId, auth.getId()));
	}
}
