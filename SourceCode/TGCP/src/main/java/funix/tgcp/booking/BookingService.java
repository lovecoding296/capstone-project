package funix.tgcp.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import funix.tgcp.tour.TourRepository;
import funix.tgcp.util.LogHelper;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

	private static final LogHelper logger = new LogHelper(BookingService.class);
	
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TourRepository tourRepository;


    // Tạo booking mới
    public Booking createBooking(Booking bookingRequest) {     
    	logger.info("");
    	tourRepository.findById(bookingRequest.getTour().getId()).orElseThrow();    	 
    	return bookingRepository.save(bookingRequest);
    }

    // Lấy danh sách bookings của người dùng
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    // Lấy danh sách bookings của Tour
    public List<Booking> getBookingsByTour(Long tourId) {
        return bookingRepository.findByTourId(tourId);
    }


	public List<Booking> findAll() {
		return bookingRepository.findAll();
	}

	public void deleteBooking(Long bookingId) {
		Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        bookingRepository.delete(booking);
	}

	public Booking findByUserIdAndTourId(Long userId, Long tourId) {
		return bookingRepository.findByUserIdAndTourId(userId, tourId);
	}

	public Optional<Booking> findById(Long bookingId) {
		return bookingRepository.findById(bookingId);
	}

	
	public ResponseEntity<?> confirmBooking(Long bookingId) {
		Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);

        if (bookingOpt.isEmpty()) {
            return ResponseEntity.status(404).body("{\"message\": \"Booking not found\", \"bookingId\": " + bookingId + "}");
        }

        Booking booking = bookingOpt.get();
        
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            return ResponseEntity.status(400).body("{\"message\": \"Booking is already confirmed\", \"bookingId\": " + bookingId + "}");
        }
        
        
        booking.setCanceledReason(null);
		booking.setStatus(BookingStatus.CONFIRMED);
		bookingRepository.save(booking);

		return ResponseEntity.ok("{\"message\": \"Booking confirmed successfully\", \"bookingId\": " + bookingId + "}");
	}
	 
	public ResponseEntity<?> cancelBooking(Long bookingId, String reason) {
		Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);

        if (bookingOpt.isEmpty()) {
            return ResponseEntity.status(404).body("{\"message\": \"Booking not found\", \"bookingId\": " + bookingId + "}");
        }

        Booking booking = bookingOpt.get();
        
        if (booking.getStatus() == BookingStatus.CANCELED) {
            return ResponseEntity.status(400).body("{\"message\": \"Booking is already canceled\", \"bookingId\": " + bookingId + "}");
        }

//        if (!booking.isCancelable()) { // Điều kiện nếu không thể hủy
//            return ResponseEntity.status(403).body("{\"error\": \"Cancellation not allowed\", \"bookingId\": " + bookingId + "}");
//        }

        logger.info(reason);
        booking.setStatus(BookingStatus.CANCELED);
        booking.setCanceledReason(reason);
        bookingRepository.save(booking);

        return ResponseEntity.ok("{\"message\": \"Booking canceled successfully\", \"bookingId\": " + bookingId + "}");

	}

	
}

