package funix.tgcp.payment;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.util.FileHelper;
import funix.tgcp.util.LogHelper;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
	
	private static final LogHelper logger = new LogHelper(PaymentController.class);


    private final PaymentService paymentService;
    
    @Autowired
    private FileHelper fileHelper;
    
    

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // API tạo payment
    @PostMapping("/create")
    public ResponseEntity<Payment> createPayment(@RequestParam("bookingId") Long bookingId, @RequestPart("payment") Payment paymentRequest,
    		@RequestPart("transactionImage") MultipartFile file
    ) {
    	
    	logger.info(" bookingId " + bookingId);
    	
	    try {
			return ResponseEntity.ok(paymentService.createPayment(bookingId, paymentRequest, file));
		} catch (IOException e) {
			e.printStackTrace();
		}        
        return null;
    }
    
	@PutMapping("/{paymentId}")
	public ResponseEntity<Payment> updatePayment(@PathVariable Long paymentId,
			@RequestPart("payment") Payment updatedPayment,
			@RequestPart(value = "transactionImage", required = false) MultipartFile transactionImage) {

		try {
			// Tìm Payment theo ID
			Optional<Payment> existingPaymentOpt = paymentService.findById(paymentId);
			if (!existingPaymentOpt.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			
			
			logger.info("");

			Payment existingPayment = existingPaymentOpt.get();

			// Cập nhật thông tin payment
			existingPayment.setAmount(updatedPayment.getAmount());
			existingPayment.setSenderAccountNumber(updatedPayment.getSenderAccountNumber());
			existingPayment.setSenderAccountName(updatedPayment.getSenderAccountName());
			existingPayment.setTransactionNote(updatedPayment.getTransactionNote());

			// Nếu có ảnh thanh toán mới, cập nhật ảnh
			if (transactionImage != null && !transactionImage.isEmpty()) {
				// Xử lý lưu ảnh nếu cần
				String imageUrl = fileHelper.uploadFile(transactionImage);
				if(imageUrl != null) {
					fileHelper.deleteFile(existingPayment.getTransactionImageUrl());
					existingPayment.setTransactionImageUrl(imageUrl);
				}
				
			}
			Payment updated = paymentService.save(existingPayment);
			return ResponseEntity.ok(updated);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

    // API guide xác nhận payment
    @PutMapping("/{paymentId}/confirm")
    public ResponseEntity<Payment> confirmPayment(
            @PathVariable Long paymentId
    ) {
        return ResponseEntity.ok(paymentService.confirmPayment(paymentId));
    }
}

