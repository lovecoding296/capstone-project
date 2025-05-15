package funix.tgcp.booking.payment;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.booking.Booking;
import funix.tgcp.booking.BookingService;
import funix.tgcp.booking.BookingStatus;
import funix.tgcp.notification.NotificationService;
import funix.tgcp.util.FileHelper;
import funix.tgcp.util.LogHelper;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PaymentService {

    private static final LogHelper logger = new LogHelper(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FileHelper fileHelper;

    @Autowired
    private BookingService bookingService;

    // Tạo payment mới
    @Transactional
    public Payment createPayment(Long bookingId, Payment paymentRequest, MultipartFile file, Long currentUserId) throws IOException {
    	
        Optional<Booking> bookingOp = bookingService.findById(bookingId);           
        Booking booking = bookingOp.get();        
        
        validateCustomerOwnership(booking, currentUserId);
        
        paymentRequest.setBooking(booking);

        // Upload file và lưu URL
        String imageUrl = fileHelper.uploadFile(file);
        paymentRequest.setTransactionImageUrl(imageUrl);

        if (paymentRequest.getType() == PaymentType.PAYMENT) {
            notificationService.sendNotification(
                    booking.getGuide(),
                    booking.getCustomer().getFullName() + " created a payment receipt, please confirm!",
                    "/guides/bookings/" + booking.getId());

            logger.info(booking.getCustomer().getFullName() + " created a payment receipt, please confirm!");
        } else {
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
    public Payment confirmPayment(Long paymentId, Long currentUserId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        Booking booking = payment.getBooking();
        
        validateGuideOwnership(booking, currentUserId);
        
        payment.setStatus(PaymentStatus.RECEIVED);
        bookingService.updateStatus(booking.getId(), BookingStatus.CONFIRMED);

        if (payment.getType() == PaymentType.PAYMENT) {
            notificationService.sendNotification(
                    booking.getCustomer(),
                    booking.getGuide().getFullName() + " confirmed your payment receipt!",
                    "/users/bookings/" + booking.getId());

            logger.info(booking.getGuide().getFullName() + " confirmed your payment receipt!");
        } else {
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

	public void delete(Long paymentId, Long currentUserId) {
		Payment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new EntityNotFoundException("Payment not found"));

//		if (payment.getType() == PaymentType.PAYMENT) {
//			validateCustomerOwnership(payment.getBooking(), currentUserId);
//		} else {
//			validateGuideOwnership(payment.getBooking(), currentUserId);
//		}

		if (payment.getTransactionImageUrl() != null) {
			try {
				fileHelper.deleteFile(payment.getTransactionImageUrl());
			} catch (IOException e) {
				logger.error("Failed to delete transaction image file " + payment.getTransactionImageUrl());
			}
		}
		paymentRepository.delete(payment);
	}
    
    private void validateCustomerOwnership(Booking booking, Long customerId) {
        if (booking == null || booking.getCustomer() == null) {
            throw new IllegalArgumentException("Booking or customer is null");
        }

        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new SecurityException("Customer ID mismatch. Booking customer does not match the provided customer ID.");
        }
    }
    
    private void validateGuideOwnership(Booking booking, Long guideId) {
        if (booking == null || booking.getGuide() == null) {
            throw new IllegalArgumentException("Booking or guide is null");
        }

        if (!booking.getGuide().getId().equals(guideId)) {
            throw new SecurityException("Guide ID mismatch. Booking guide does not match the provided guide ID.");
        }
    }
}
