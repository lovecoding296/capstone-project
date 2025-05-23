package funix.tgcp.booking;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import funix.tgcp.util.LogHelper;

@Controller
public class BookingController {

	private static final LogHelper logger = new LogHelper(BookingController.class);

	@Autowired
	private BookingService bookingService;

	@GetMapping("/users/bookings/{bookingId}")
	public String showBooking(@PathVariable Long bookingId, Model model) {
		Optional<Booking> bookingOp = bookingService.findById(bookingId);
    	


		if (bookingOp.isPresent()) {
			logger.info("pass a booking");
			long completedCount = bookingService.countCompletedByGuideId(bookingOp.get().getGuide().getId());			
			
			model.addAttribute("booking", bookingOp.get());
			model.addAttribute("completedCount", completedCount);
		}

		return "booking/user-booking";
	}

	@GetMapping("/guides/bookings/{bookingId}")
	public String showBookingForGuide(@PathVariable Long bookingId, Model model) {
		Optional<Booking> bookingOp = bookingService.findById(bookingId);

		if (bookingOp.isPresent()) {
			logger.info("pass a booking");
			long rentedCount = bookingService.countCompletedByUserId(bookingOp.get().getCustomer().getId());			
			model.addAttribute("rentedCount", rentedCount);
			model.addAttribute("booking", bookingOp.get());
		}

		return "booking/guide-booking";
	}
}
