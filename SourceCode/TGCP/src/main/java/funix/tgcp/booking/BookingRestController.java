package funix.tgcp.booking;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.util.LogHelper;

@RestController
public class BookingRestController {
	
	private static final LogHelper logger = new LogHelper(BookingRestController.class);

    @Autowired
    private BookingService bookingService;

    // API tạo booking
    @PostMapping("/api/bookings/create")
    public ResponseEntity<?> createBooking(
            @RequestBody Booking bookingRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("userDetails " + userDetails);

        bookingRequest.setCustomer(userDetails.getUser());

        Optional<Booking> savedBooking = bookingService.createBooking(bookingRequest);

        if (savedBooking.isPresent()) {
            return ResponseEntity.ok(savedBooking.get());
        }

        return ResponseEntity.status(400).body(Map.of("message", "Failed to create booking"));
    }

    
    

    @GetMapping("/api/bookings")
    public Page<Booking> findBookingByCustomerAndFilter(
	        @RequestParam(required = false) String destination,
	        @RequestParam(required = false) LocalDate startDate,
	        @RequestParam(required = false) LocalDate endDate, 
	        @RequestParam(required = false) String guide, 
	        @RequestParam(required = false) BookingStatus status,
	        @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
	        @AuthenticationPrincipal CustomUserDetails userDetails){
    	logger.info("userDetails " + userDetails);
			
    	Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));
    	return bookingService.findBookingByCustomerAndFilter(
				userDetails.getId(),
				destination,
				startDate,
				endDate,
				guide,
				status,
				pageable);
		    	    	
    }
    
    // API lấy tất cả booking nhận được của guide
    @GetMapping("/api/guides/bookings")
    public Page<Booking> findBookingsByGuideAndFilter(
    		@RequestParam(required = false) String destination,
	        @RequestParam(required = false) LocalDate startDate,
	        @RequestParam(required = false) LocalDate endDate, 
	        @RequestParam(required = false) String user, 
	        @RequestParam(required = false) BookingStatus status,
	        @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
	        @AuthenticationPrincipal CustomUserDetails userDetails) {      
        
    	logger.info("userDetails " + userDetails);
    	
    	Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));
    	return bookingService.findBookingByGuideAndFilter(
				userDetails.getId(),
				destination,
				startDate,
				endDate,
				user,
				status,
				pageable);
    }

    @GetMapping("/api/bookings/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable Long bookingId) {
        Optional<Booking> booking = bookingService.findById(bookingId);
        
        if (booking.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(booking.get());
    }
    


    // API xác nhận booking
    @PutMapping("/api/bookings/{bookingId}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable Long bookingId) {
    	boolean success = bookingService.confirmBooking(bookingId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Booking confirmed successfully", "bookingId", bookingId));
        }
        return ResponseEntity.status(400).body(Map.of("message", "Failed to confirm booking", "bookingId", bookingId));
    }

    @PutMapping("/api/bookings/{bookingId}/complete")
    public ResponseEntity<?> completeBooking(@PathVariable Long bookingId) {
        boolean success = bookingService.completeBooking(bookingId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Booking completed successfully", "bookingId", bookingId));
        }
        return ResponseEntity.status(400).body(Map.of("message", "Failed to complete booking", "bookingId", bookingId));
    }

    @PutMapping("/api/bookings/{bookingId}/cancel-by-user")
    public ResponseEntity<?> cancelBookingByUser(@PathVariable Long bookingId, @RequestBody Map<String, String> requestBody) {
        boolean success = bookingService.cancelBookingByUser(bookingId, requestBody.get("reason"));
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Booking canceled successfully", "bookingId", bookingId));
        }
        return ResponseEntity.status(400).body(Map.of("message", "Failed to cancel booking", "bookingId", bookingId));
    }

    @PutMapping("/api/bookings/{bookingId}/cancel-by-guide")
    public ResponseEntity<?> cancelBookingByGuide(@PathVariable Long bookingId, @RequestBody Map<String, String> requestBody) {
        boolean success = bookingService.cancelBookingByGuide(bookingId, requestBody.get("reason"));
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Booking canceled successfully", "bookingId", bookingId));
        }
        return ResponseEntity.status(400).body(Map.of("message", "Failed to cancel booking", "bookingId", bookingId));
    }

    @PutMapping("/api/bookings/{bookingId}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable Long bookingId, @RequestBody Map<String, String> requestBody) {
        boolean success = bookingService.rejectBooking(bookingId, requestBody.get("reason"));
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Booking rejected successfully", "bookingId", bookingId));
        }
        return ResponseEntity.status(400).body(Map.of("message", "Failed to reject booking", "bookingId", bookingId));
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

