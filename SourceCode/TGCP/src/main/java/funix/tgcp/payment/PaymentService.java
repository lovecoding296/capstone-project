package funix.tgcp.payment;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.booking.Booking;
import funix.tgcp.booking.BookingRepository;
import funix.tgcp.util.FileUploadHelper;

@Service
public class PaymentService {

	@Autowired
    private PaymentRepository paymentRepository;
	
	@Autowired
    private BookingRepository bookingRepository;
    
	@Autowired
	private FileUploadHelper fileUploadHelper;




    // Tạo payment mới
    public Payment createPayment(Payment paymentRequest, MultipartFile file) throws IOException {
    	
    	Long userId = paymentRequest.getBooking().getUser().getId();
    	Long tourId = paymentRequest.getBooking().getTour().getId();
    	Booking booking = bookingRepository.findByUserIdAndTourId(userId, tourId);
    	
        if(booking != null) {
        	// Lưu ảnh giao dịch
            String imageUrl = fileUploadHelper.uploadFile(file);
            paymentRequest.setTransactionImageUrl(imageUrl);
            paymentRequest.setBooking(booking);

            return paymentRepository.save(paymentRequest);
        }        
        return null;
    }

    // Guide xác nhận payment
    public Payment confirmPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow();

        // Cập nhật status thành RECEIVED khi thanh toán được xác nhận
        payment.setStatus(PaymentStatus.RECEIVED);

        return paymentRepository.save(payment);
    }
}
