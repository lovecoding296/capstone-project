package funix.tgcp.payment;
import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.booking.Booking;
import funix.tgcp.booking.BookingRepository;
import funix.tgcp.booking.BookingService;
import funix.tgcp.util.FileHelper;

@Service
public class PaymentService {

	@Autowired
    private PaymentRepository paymentRepository;
	
	@Autowired
    private BookingRepository bookingRepository;
    
	@Autowired
	private FileHelper fileHelper;

	@Autowired
    private BookingService bookingService;


    // Tạo payment mới
    public Payment createPayment(Long bookingId, Payment paymentRequest, MultipartFile file) throws IOException {
    	
    	Optional<Booking> booking = bookingService.findById(bookingId);
    	paymentRequest.setBooking(booking.get());
        
        String imageUrl = fileHelper.uploadFile(file);
        paymentRequest.setTransactionImageUrl(imageUrl);

        return paymentRepository.save(paymentRequest);
        
    }

    // Guide xác nhận payment
    public Payment confirmPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow();

        // Cập nhật status thành RECEIVED khi thanh toán được xác nhận
        payment.setStatus(PaymentStatus.RECEIVED);
        return paymentRepository.save(payment);
    }

	public Optional<Payment> findById(Long paymentId) {
		return paymentRepository.findById(paymentId);
	}

	public Payment save(Payment existingPayment) {
		return paymentRepository.save(existingPayment);
	}
}
