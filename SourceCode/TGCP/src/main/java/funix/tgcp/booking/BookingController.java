package funix.tgcp.booking;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.util.LogHelper;

@Controller
public class BookingController {

	private static final LogHelper logger = new LogHelper(BookingController.class);

	@Autowired
	private BookingService bookingService;

	@GetMapping("/users/bookings/{bookingId}")
	public String showBooking(@PathVariable Long bookingId, 
	                          Model model, 
	                          @AuthenticationPrincipal CustomUserDetails auth,
	                          RedirectAttributes redirectAttributes) {
	    Optional<Booking> bookingOp = bookingService.findById(bookingId);

	    if (bookingOp.isEmpty()) {
	        logger.warn("Booking not found with id: {}", bookingId);
	        redirectAttributes.addFlashAttribute("errorMessage", "Page not found!");
	        return "redirect:/error";
	    }

	    Booking booking = bookingOp.get();

	    if (!auth.isAdmin() && !booking.getCustomer().equals(auth.getUser())) {
	        logger.warn("Unauthorized access attempt to booking id: {} by user: {}", bookingId, auth.getUsername());
	        redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to access this page!");
	        return "redirect:/error";
	    }

	    logger.info("User {} is viewing booking id: {}", auth.getUsername(), bookingId);

	    long completedCount = 0;
	    if (booking.getGuide() != null) {
	        completedCount = bookingService.countCompletedByGuideId(booking.getGuide().getId());
	    }

	    model.addAttribute("booking", booking);
	    model.addAttribute("completedCount", completedCount);

	    return "booking/user-booking";
	}


	@GetMapping("/guides/bookings/{bookingId}")
	public String showBookingForGuide(@PathVariable Long bookingId, 
			Model model,
			@AuthenticationPrincipal CustomUserDetails auth,
			RedirectAttributes redirectAttributes) {
	    Optional<Booking> bookingOp = bookingService.findById(bookingId);

	    if (bookingOp.isEmpty()) {
	    	logger.warn("Booking not found with id: {}", bookingId);
	        redirectAttributes.addFlashAttribute("errorMessage", "Page not found!");
	        return "redirect:/error";
	    }

	    Booking booking = bookingOp.get();
	    logger.info("Guide is viewing booking id: {}", bookingId);
	    
	    if (!auth.isAdmin() && !booking.getGuide().equals(auth.getUser())) {
	        logger.warn("Unauthorized access attempt to booking id: {} by guide: {}", bookingId, auth.getUsername());
	        redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to access this page!");
	        return "redirect:/error";
	    }

	    long rentedCount = 0;
	    if (booking.getCustomer() != null) {
	        rentedCount = bookingService.countCompletedByUserId(booking.getCustomer().getId());
	    }

	    model.addAttribute("booking", booking);
	    model.addAttribute("rentedCount", rentedCount);

	    return "booking/guide-booking";
	}

}
