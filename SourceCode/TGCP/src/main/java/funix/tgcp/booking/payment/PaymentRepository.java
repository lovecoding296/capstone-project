package funix.tgcp.booking.payment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Các phương thức truy vấn cần thiết
}

