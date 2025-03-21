package funix.tgcp.booking;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // API tạo booking
    @PostMapping("/create")
    public ResponseEntity<Booking> createBooking(
            @RequestParam Long tourId,
            @RequestParam Long userId,
            @RequestParam String note
    ) {
        return ResponseEntity.ok(bookingService.createBooking(tourId, userId, note));
    }

    // API lấy tất cả booking của người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }

    // API lấy tất cả booking của tour
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<Booking>> getBookingsByTour(@PathVariable Long tourId) {
        return ResponseEntity.ok(bookingService.getBookingsByTour(tourId));
    }

    // API xác nhận booking
    @PutMapping("/{bookingId}/confirm")
    public ResponseEntity<Booking> confirmBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.confirmBooking(bookingId));
    }
}

