package funix.tca.service;

import funix.tca.entity.Payment;
import funix.tca.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Lưu giao dịch thanh toán
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    // Lấy giao dịch thanh toán theo ID
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    // Lấy tất cả giao dịch thanh toán
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    // Lấy các giao dịch thanh toán theo chuyến đi
    public List<Payment> findByTripId(Long tripId) {
        return paymentRepository.findByTripId(tripId);
    }

    // Lấy các giao dịch thanh toán của một người thanh toán
    public List<Payment> findByPayerId(Long payerId) {
        return paymentRepository.findByPayerId(payerId);
    }

    // Lấy các giao dịch thanh toán của một người nhận
    public List<Payment> findByReceiverId(Long receiverId) {
        return paymentRepository.findByReceiverId(receiverId);
    }

    // Lấy các giao dịch thanh toán đã được xác nhận
    public List<Payment> findByConfirmed(boolean confirmed) {
        return paymentRepository.findByConfirmed(confirmed);
    }

    // Cập nhật giao dịch thanh toán
    public Payment update(Payment payment) {
        return paymentRepository.save(payment);
    }

    // Xóa giao dịch thanh toán theo ID
    public void deleteById(Long id) {
        paymentRepository.deleteById(id);
    }
}
