package funix.tca.trip.expense;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripExpenseRepository extends JpaRepository<TripExpense, Long> {
    List<TripExpense> findByTripId(Long tripId); // Lấy các khoản chi phí của chuyến đi
    List<TripExpense> findByPaidById(Long paidById); // Lấy các khoản chi phí đã thanh toán bởi một người
}
