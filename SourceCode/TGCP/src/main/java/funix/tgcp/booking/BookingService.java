package funix.tgcp.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import funix.tgcp.tour.Tour;
import funix.tgcp.tour.TourRepository;
import funix.tgcp.user.User;
import funix.tgcp.user.UserRepository;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private UserRepository userRepository;

    // Tạo booking mới
    public Booking createBooking(Long tourId, Long userId, String note) {
        Tour tour = tourRepository.findById(tourId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        Booking booking = new Booking();
        booking.setTour(tour);
        booking.setUser(user);
        booking.setStatus(BookingStatus.PENDING); // Mặc định status là PENDING

        return bookingRepository.save(booking);
    }

    // Lấy danh sách bookings của người dùng
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    // Lấy danh sách bookings của chuyến đi
    public List<Booking> getBookingsByTour(Long tourId) {
        return bookingRepository.findByTourId(tourId);
    }

    // Xác nhận booking
    public Booking confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        booking.setStatus(BookingStatus.CONFIRMED);

        return bookingRepository.save(booking);
    }
}

