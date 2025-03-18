package funix.tgcp.payment;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.booking.Booking;
import funix.tgcp.booking.BookingRepository;
import funix.tgcp.util.FileUploadHelper;

@Service
public class PaymentService {

	
    private PaymentRepository paymentRepository;
    private BookingRepository bookingRepository;
    private FileUploadHelper fileUploadHelper;




    // Tạo payment mới
    public Payment createPayment(Long bookingId, double amount, String senderAccountNumber,
                                 String senderAccountName, String transactionNote, MultipartFile file) throws IOException {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PENDING); // Mặc định status là PENDING
        payment.setSenderAccountNumber(senderAccountNumber);
        payment.setSenderAccountName(senderAccountName);
        payment.setTransactionNote(transactionNote);

        // Lưu ảnh giao dịch
        String imageUrl = fileUploadHelper.uploadFile(file);
        payment.setTransactionImageUrl(imageUrl);

        return paymentRepository.save(payment);
    }

    // Guide xác nhận payment
    public Payment confirmPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow();

        // Cập nhật status thành RECEIVED khi thanh toán được xác nhận
        payment.setStatus(PaymentStatus.RECEIVED);

        return paymentRepository.save(payment);
    }
}
