package funix.tgcp.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import funix.tgcp.payment.PaymentController;
import funix.tgcp.tour.Tour;
import funix.tgcp.tour.TourRepository;
import funix.tgcp.user.User;
import funix.tgcp.user.UserRepository;
import funix.tgcp.util.LogHelper;

import java.util.List;

@Service
public class BookingService {

	private static final LogHelper logger = new LogHelper(BookingService.class);
	
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private UserRepository userRepository;

    // Tạo booking mới
    public Booking createBooking(Booking bookingRequest) {     
    	logger.info("");
    	tourRepository.findById(bookingRequest.getTour().getId()).orElseThrow();
    	if(bookingRepository.existsByUserAndTour(bookingRequest.getUser(), bookingRequest.getTour())) {
    		logger.info("create booking");
    		return bookingRepository.save(bookingRequest);
    	}
        return null;
    }

    // Lấy danh sách bookings của người dùng
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    // Lấy danh sách bookings của Tour
    public List<Booking> getBookingsByTour(Long tourId) {
        return bookingRepository.findByTourId(tourId);
    }

    // Xác nhận booking
    public Booking confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        booking.setStatus(BookingStatus.CONFIRMED);

        return bookingRepository.save(booking);
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

	
}

