package funix.tgcp.booking;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.user.User;
import funix.tgcp.util.LogHelper;

@RestController
public class BookingController {
	
	private static final LogHelper logger = new LogHelper(BookingController.class);

    @Autowired
    private BookingService bookingService;

    // API tạo booking
    @PostMapping("/api/bookings/create")
    public ResponseEntity<Booking> createBooking(@RequestBody Booking bookingRequest) {
    	
    	CustomUserDetails userDetails = CustomUserDetails.getCurrentUserDetails();
    	logger.info("userDetails " + userDetails);
    	if(userDetails != null ) {
    		bookingRequest.setUser(userDetails.getUser());
    		return ResponseEntity.ok(bookingService.createBooking(bookingRequest));
    	} else {
    		User user = new User();
    		user.setId((long)1);
    		bookingRequest.setUser(user);
    	}    	
        return ResponseEntity.ok(bookingService.createBooking(bookingRequest));
    }
    
    

    // API lấy tất cả booking của người dùng
    @GetMapping("/api/bookings")
    public ResponseEntity<List<Booking>> getBookings() {
    	
    	CustomUserDetails userDetails = CustomUserDetails.getCurrentUserDetails();
    	logger.info("userDetails " + userDetails);
    	
    	List<Booking> bookings;    	
		if (userDetails != null) {
			logger.info("is admin " + userDetails.isAdmin());
			if(userDetails.isAdmin()) {
				bookings = bookingService.findAll();
			} else {
				Long userId = userDetails.getId();
				bookings = bookingService.getBookingsByUser(userId);
			}			
		}
		else {
			logger.info("not logged in -> get all bookings");
			bookings = bookingService.findAll();
		}    	
    	
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/api/bookings/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable Long bookingId) {
        Optional<Booking> booking = bookingService.findById(bookingId);
        
        if (booking.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(booking.get());
    }
    
    
    
    @GetMapping("/api/tours/{tourId}/bookings/user")
    public ResponseEntity<?> getBookingsByUserAndTour(@PathVariable Long tourId) {
    	CustomUserDetails userDetails = CustomUserDetails.getCurrentUserDetails();
    	logger.info("userDetails " + userDetails);
    	
		if (userDetails != null) {
			Long userId = userDetails.getId();
			return ResponseEntity.ok(bookingService.findByUserIdAndTourId(userId, tourId));
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");
    }
    
    // API lấy tất cả booking của tour
    @GetMapping("/api/tours/{tourId}/bookings")
    public ResponseEntity<List<Booking>> getBookingsByTour(@PathVariable Long tourId) {
        CustomUserDetails userDetails = CustomUserDetails.getCurrentUserDetails();
    	logger.info("userDetails " + userDetails);
    	
    	List<Booking> bookings;    	
		if (userDetails != null) {
			logger.info("is admin " + userDetails.isAdmin());
			if(userDetails.isAdmin()) {
				bookings = bookingService.findAll();
			} else {
				Long userId = userDetails.getId();
				bookings = bookingService.getBookingsByTour(tourId);
			}			
		}
		else {
			logger.info("not logged in -> get all bookings");
			bookings = bookingService.findAll();
		}    	
    	
        return ResponseEntity.ok(bookings);
    }
    
    // API lấy tất cả booking nhận được của guide
    @GetMapping("/api/guides/bookings")
    public ResponseEntity<List<Booking>> getBookingsByGuide() {      
        
        CustomUserDetails userDetails = CustomUserDetails.getCurrentUserDetails();
    	logger.info("userDetails " + userDetails);
    	
    	List<Booking> bookings;    	
		if (userDetails != null) {
			logger.info("is admin " + userDetails.isAdmin());
			if(userDetails.isAdmin()) {
				bookings = bookingService.findAll();
			} else {
				Long userId = userDetails.getId();
				bookings = bookingService.getBookingsByTour(userId);
			}			
		}
		else {
			logger.info("not logged in -> get all bookings");
			bookings = bookingService.findAll();
		}    	
    	
        return ResponseEntity.ok(bookings);
    }

    // API xác nhận booking
    @PutMapping("/api/bookings/{bookingId}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable Long bookingId) {
    	return bookingService.confirmBooking(bookingId);
    }
    
    @PutMapping("/api/bookings/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId, @RequestBody Map<String, String> requestBody) {
        logger.info(requestBody.get("reason"));
    	return bookingService.cancelBooking(bookingId, requestBody.get("reason"));
    }
    

    @DeleteMapping("/api/bookings/{bookingId}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long bookingId) {
    	//bookingService.deleteBooking(bookingId);
        try {
            bookingService.deleteBooking(bookingId);
            return ResponseEntity.ok("Booking đã được xóa thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi xóa booking.");
        }
    }
    
    @GetMapping("/api/bookings/delete/{bookingId}")
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

