package funix.tgcp.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import funix.tgcp.trip.Trip;
import funix.tgcp.trip.TripRepository;
import funix.tgcp.user.User;
import funix.tgcp.user.UserRepository;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    // Tạo booking mới
    public Booking createBooking(Long tripId, Long userId, String note) {
        Trip trip = tripRepository.findById(tripId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        Booking booking = new Booking();
        booking.setTrip(trip);
        booking.setUser(user);
        booking.setStatus(BookingStatus.PENDING); // Mặc định status là PENDING

        return bookingRepository.save(booking);
    }

    // Lấy danh sách bookings của người dùng
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    // Lấy danh sách bookings của chuyến đi
    public List<Booking> getBookingsByTrip(Long tripId) {
        return bookingRepository.findByTripId(tripId);
    }

    // Xác nhận booking
    public Booking confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        booking.setStatus(BookingStatus.CONFIRMED);

        return bookingRepository.save(booking);
    }
}

