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
    public ResponseEntity<?> createBooking(@RequestBody Booking bookingRequest, 
    		@AuthenticationPrincipal CustomUserDetails auth) {    
    	
    	bookingRequest.setCustomer(auth.getUser());    	
        Optional<Booking> savedBooking = bookingService.createBooking(bookingRequest);
        if (savedBooking.isPresent()) {
            return ResponseEntity.ok(savedBooking.get());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Failed to create booking"));
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
            @AuthenticationPrincipal CustomUserDetails auth) {

        logger.info("auth " + auth);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));
        return bookingService.findBookingByCustomerAndFilter(
        		auth.getId(),
                destination,
                startDate,
                endDate,
                guide,
                status,
                pageable);
    }

    @GetMapping("/api/guides/bookings")
    public ResponseEntity<?> findBookingsByGuideAndFilter(
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String user,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @AuthenticationPrincipal CustomUserDetails auth) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));
        Page<Booking> bookings = bookingService.findBookingByGuideAndFilter(
                auth.getId(),
                destination,
                startDate,
                endDate,
                user,
                status,
                pageable);

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

    @PutMapping("/api/bookings/{bookingId}/confirm")
    public ResponseEntity<?> confirmBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomUserDetails auth) {

        bookingService.confirmBooking(bookingId, auth.getId());

        return ResponseEntity.ok(Map.of(
                "message", "Booking confirmed successfully",
                "bookingId", bookingId
        ));
    }

    @PutMapping("/api/bookings/{bookingId}/complete")
    public ResponseEntity<?> completeBooking(@PathVariable Long bookingId, 
    		@AuthenticationPrincipal CustomUserDetails auth) {
        bookingService.completeBooking(bookingId, auth.getId());
        return ResponseEntity.ok(Map.of("message", "Booking completed successfully", "bookingId", bookingId));
    }

    @PutMapping("/api/bookings/{bookingId}/cancel-by-user")
    public ResponseEntity<?> cancelBookingByUser(@PathVariable Long bookingId, @RequestBody Map<String, String> requestBody, 
    		@AuthenticationPrincipal CustomUserDetails auth) {
        bookingService.cancelBookingByUser(bookingId, requestBody.get("reason"), auth.getId());
        return ResponseEntity.ok(Map.of("message", "Booking canceled successfully", "bookingId", bookingId));
    }

    @PutMapping("/api/bookings/{bookingId}/cancel-by-guide")
    public ResponseEntity<?> cancelBookingByGuide(@PathVariable Long bookingId, @RequestBody Map<String, String> requestBody, 
    		@AuthenticationPrincipal CustomUserDetails auth) {
        bookingService.cancelBookingByGuide(bookingId, requestBody.get("reason"), auth.getId());
        return ResponseEntity.ok(Map.of("message", "Booking canceled successfully", "bookingId", bookingId));
    }

    @PutMapping("/api/bookings/{bookingId}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable Long bookingId, @RequestBody Map<String, String> requestBody, 
    		@AuthenticationPrincipal CustomUserDetails auth) {
        bookingService.rejectBooking(bookingId, requestBody.get("reason"), auth.getId());
        return ResponseEntity.ok(Map.of("message", "Booking rejected successfully", "bookingId", bookingId));
    }

    @DeleteMapping("/api/bookings/{bookingId}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long bookingId, 
    		@AuthenticationPrincipal CustomUserDetails auth) {
        bookingService.deleteBooking(bookingId, auth.getId());
        return ResponseEntity.ok(Map.of("message", "Booking has been deleted successfully."));
    }
}
