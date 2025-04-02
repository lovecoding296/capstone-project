package funix.tgcp.booking;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.home.controller.HomeController;
import funix.tgcp.util.LogHelper;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
	
	private static final LogHelper logger = new LogHelper(BookingController.class);

    @Autowired
    private BookingService bookingService;
    
    
 // API lấy tất cả booking của người dùng
    @GetMapping()
    public ResponseEntity<List<Booking>> findAll() {
        return ResponseEntity.ok(bookingService.findAll());
    }

    // API tạo booking
    @PostMapping("/create")
    public ResponseEntity<Booking> createBooking(@RequestBody Booking bookingRequest) {
    	logger.info("");
    	//TODO
    	//lấy userId từ loggedInUser
        return ResponseEntity.ok(bookingService.createBooking(bookingRequest));
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
    
    @GetMapping("/users/tour/{tourId}")
    public ResponseEntity<?> getBookingsByUserAndTour(@PathVariable Long tourId) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof CustomUserDetails) {
			CustomUserDetails userDetails = (CustomUserDetails) principal;
			Long userId = userDetails.getId();

			return ResponseEntity.ok(bookingService.findByUserIdAndTourId(userId, tourId));
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");
    }

    // API xác nhận booking
    @PutMapping("/{bookingId}/confirm")
    public ResponseEntity<Booking> confirmBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.confirmBooking(bookingId));
    }
    
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long bookingId) {
    	//bookingService.deleteBooking(bookingId);
        try {
            bookingService.deleteBooking(bookingId);
            return ResponseEntity.ok("Booking đã được xóa thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi xóa booking.");
        }
    }
    
    @GetMapping("/delete/{bookingId}")
    public ResponseEntity<String> testDeleteBooking(@PathVariable Long bookingId) {
    	//bookingService.deleteBooking(bookingId);
        try {
            bookingService.deleteBooking(bookingId);
            return ResponseEntity.ok("Booking đã được xóa thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi xóa booking.");
        }
    }
}

