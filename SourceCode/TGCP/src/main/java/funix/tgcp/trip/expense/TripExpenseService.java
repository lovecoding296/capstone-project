package funix.tgcp.trip.expense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TripExpenseService {

    @Autowired
    private TripExpenseRepository tripExpenseRepository;

    // Lưu một khoản chi phí
    public TripExpense save(TripExpense tripExpense) {
        return tripExpenseRepository.save(tripExpense);
    }

    // Lấy một khoản chi phí theo ID
    public Optional<TripExpense> findById(Long id) {
        return tripExpenseRepository.findById(id);
    }

    // Lấy tất cả các khoản chi phí
    public List<TripExpense> findAll() {
        return tripExpenseRepository.findAll();
    }

    // Lấy các khoản chi phí theo chuyến đi
    public List<TripExpense> findByTripId(Long tripId) {
        return tripExpenseRepository.findByTripId(tripId);
    }

    // Lấy các khoản chi phí đã thanh toán bởi một người
    public List<TripExpense> findByPaidById(Long paidById) {
        return tripExpenseRepository.findByPaidById(paidById);
    }

    // Cập nhật khoản chi phí
    public TripExpense update(TripExpense tripExpense) {
        return tripExpenseRepository.save(tripExpense);
    }

    // Xóa khoản chi phí theo ID
    public void deleteById(Long id) {
        tripExpenseRepository.deleteById(id);
    }
}
