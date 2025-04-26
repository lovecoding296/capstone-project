package funix.tgcp.payment;
import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.booking.Booking;
import funix.tgcp.booking.BookingRestController;
import funix.tgcp.booking.BookingRepository;
import funix.tgcp.booking.BookingService;
import funix.tgcp.notification.NotificationService;
import funix.tgcp.util.FileHelper;
import funix.tgcp.util.LogHelper;

@Service
public class PaymentService {
	
	private static final LogHelper logger = new LogHelper(PaymentService.class);


	@Autowired
    private PaymentRepository paymentRepository;
	
	@Autowired 
	NotificationService notificationService;
    
	@Autowired
	private FileHelper fileHelper;

	@Autowired
    private BookingService bookingService;


    // Tạo payment mới
	@Transactional
    public Payment createPayment(Long bookingId, Payment paymentRequest, MultipartFile file) throws IOException {
    	
    	Optional<Booking> bookingOp = bookingService.findById(bookingId);
    	Booking booking = bookingOp.get();
    	
    	
    	paymentRequest.setBooking(booking);
    	
        
        String imageUrl = fileHelper.uploadFile(file);
        paymentRequest.setTransactionImageUrl(imageUrl);
        
        if(paymentRequest.getType() == PaymentType.PAYMENT) {
            notificationService.sendNotification(
            		booking.getGuide(), 
            		booking.getCustomer().getFullName() + " created a payment receipt, please confirm!", 
            		"/guides/bookings/" + booking.getId());
            
            logger.info(booking.getCustomer().getFullName() + " created a payment receipt, please confirm!");
        }
        else {
            notificationService.sendNotification(
            		booking.getCustomer(), 
            		booking.getGuide().getFullName() + " created a refund receipt, please confirm!", 
            		"/users/bookings/" + booking.getId());
            
            logger.info(booking.getGuide().getFullName() + " created a refund receipt, please confirm!");
        }

        
        return paymentRepository.save(paymentRequest);
        
    }

    // Guide xác nhận payment
	@Transactional
    public Payment confirmPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow();

        // Cập nhật status thành RECEIVED khi thanh toán được xác nhận
        payment.setStatus(PaymentStatus.RECEIVED);
        Booking booking = payment.getBooking();
        
        if(payment.getType() == PaymentType.PAYMENT) {
        	notificationService.sendNotification(
            		booking.getCustomer(), 
            		booking.getGuide().getFullName() + " confirmed your payment receipt!", 
            		"/users/bookings/" + booking.getId());
            
            logger.info(booking.getGuide().getFullName() + " confirmed your payment receipt!");
        }
        else {
        	notificationService.sendNotification(
            		booking.getGuide(), 
            		booking.getCustomer().getFullName() + " confirmed your refund receipt!", 
            		"/users/bookings/" + booking.getId());
            
            logger.info(booking.getCustomer().getFullName() + " confirmed refund payment receipt!");
        }
        
        return paymentRepository.save(payment);
    }

	public Optional<Payment> findById(Long paymentId) {
		return paymentRepository.findById(paymentId);
	}

	public Payment save(Payment existingPayment) {
		return paymentRepository.save(existingPayment);
	}

	public void delete(Long paymentId) {
		Payment payment = paymentRepository.findById(paymentId).orElseThrow();
		if(payment.getTransactionImageUrl() != null) {
			try {
				fileHelper.deleteFile(payment.getTransactionImageUrl());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		paymentRepository.delete(payment);
	}
}
