package funix.tca.repository;

import funix.tca.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByTripId(Long tripId); // Lấy các giao dịch thanh toán của một chuyến đi
    List<Payment> findByPayerId(Long payerId); // Lấy các giao dịch thanh toán của một người thanh toán
    List<Payment> findByReceiverId(Long receiverId); // Lấy các giao dịch thanh toán của một người nhận
    List<Payment> findByConfirmed(boolean confirmed); // Lấy các giao dịch đã xác nhận
}
