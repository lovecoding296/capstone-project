package funix.tgcp.payment;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.booking.Booking;
import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.guide.request.GuideRequestController;
import funix.tgcp.user.User;
import funix.tgcp.util.LogHelper;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
	
	private static final LogHelper logger = new LogHelper(PaymentController.class);


    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // API tạo payment
    @PostMapping("/create")
    public ResponseEntity<Payment> createPayment(@RequestPart("payment") Payment paymentRequest, 
    		@RequestPart("transactionImage") MultipartFile file
    ) {
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;         

			Booking booking = paymentRequest.getBooking();
			booking.setUser(userDetails.getuser());
			
			paymentRequest.setBooking(booking);
		    try {
		    	logger.info("");
				return ResponseEntity.ok(paymentService.createPayment(paymentRequest, file));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}        
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

