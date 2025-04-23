package funix.tgcp.booking;

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
    	
//    	User currentUser = new User();
//    	if(userDetails != null ) {
//    		currentUser.setId(userDetails.getId());
//    		
//    	} else {
//    		currentUser.setId((long)1);
//    	}    	
    	bookingRequest.setCustomer(userDetails.getUser());
		return ResponseEntity.ok(bookingService.createBooking(bookingRequest));
    }
    
    

    // API lấy tất cả booking của người dùng
    @GetMapping("/api/bookings")
    public ResponseEntity<?> getBookings() {
    	CustomUserDetails userDetails = CustomUserDetails.getCurrentUserDetails();
    	logger.info("userDetails " + userDetails);
    	
		if (userDetails != null) {
			logger.info("is admin " + userDetails.isAdmin());
			if(userDetails.isAdmin()) {
				return bookingService.findAll();
			} else {
				Long userId = userDetails.getId();
				return bookingService.getBookingsByCustomer(userId);
			}			
		}
		else {
			logger.info("not logged in -> get all bookings");
			return bookingService.findAll();
		}    	    	
    }

    @GetMapping("/api/bookings/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable Long bookingId) {
        Optional<Booking> booking = bookingService.findById(bookingId);
        
        if (booking.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(booking.get());
    }
    
    // API lấy tất cả booking nhận được của guide
    @GetMapping("/api/guides/bookings")
    public ResponseEntity<?> getBookingsByGuide() {      
        
        CustomUserDetails userDetails = CustomUserDetails.getCurrentUserDetails();
    	logger.info("userDetails " + userDetails);
    	
    	if (userDetails != null) {
			logger.info("is admin " + userDetails.isAdmin());
			if(userDetails.isAdmin()) {
				return bookingService.findAll();
			} else {
				Long userId = userDetails.getId();
				return bookingService.getBookingsByGuide(userId);
			}			
		}
		else {
			logger.info("not logged in -> get all bookings");
			return bookingService.findAll();
		}
    }

    // API xác nhận booking
    @PutMapping("/api/bookings/{bookingId}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable Long bookingId) {
    	return bookingService.confirmBooking(bookingId);
    }
    
    // API xác nhận complete booking
    @PutMapping("/api/bookings/{bookingId}/complete")
    public ResponseEntity<?> completeBooking(@PathVariable Long bookingId) {
    	return bookingService.completeBooking(bookingId);
    }
    
    @PutMapping("/api/bookings/{bookingId}/cancel-by-user")
    public ResponseEntity<?> cancelBookingByUser(@PathVariable Long bookingId, @RequestBody Map<String, String> requestBody) {
        logger.info(requestBody.get("reason"));
    	return bookingService.cancelBookingByUser(bookingId, requestBody.get("reason"));
    }
    
    @PutMapping("/api/bookings/{bookingId}/cancel-by-guide")
    public ResponseEntity<?> cancelBookingByGuide(@PathVariable Long bookingId, @RequestBody Map<String, String> requestBody) {
        logger.info(requestBody.get("reason"));
    	return bookingService.cancelBookingByGuide(bookingId, requestBody.get("reason"));
    }
    
    @PutMapping("/api/bookings/{bookingId}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable Long bookingId, @RequestBody Map<String, String> requestBody) {
        logger.info(requestBody.get("reason"));
    	return bookingService.rejectBooking(bookingId, requestBody.get("reason"));
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

