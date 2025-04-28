package funix.tgcp.guide.income;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import funix.tgcp.booking.Booking;
import funix.tgcp.booking.BookingRepository;
import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.util.LogHelper;

@RestController
public class GuideIncomeController {
	
	private static final LogHelper logger = new LogHelper(GuideIncomeController.class);

	
	@Autowired
	private BookingRepository bookingRepository;

	@GetMapping("/api/guides/{guideId}/income-summary")
	public Map<String, Object> getIncomeSummaryById(@PathVariable Long guideId) {
	    return buildIncomeSummary(guideId);
	}

	@GetMapping("/api/guides/income-summary")
	public Map<String, Object> getIncomeSummary(@AuthenticationPrincipal CustomUserDetails userDetails) {
		
	    Long guideId = Optional.ofNullable(userDetails)
	                           .map(details -> details.getUser().getId())
	                           .orElse(5L); // fallback ID
	    return buildIncomeSummary(guideId);
	}

	
	private Map<String, Object> buildIncomeSummary(Long guideId) {
	    logger.info("Generating income summary for guide ID: {}", guideId);

	    List<Booking> completedBookings = bookingRepository.findCompletedByGuideId(guideId);

	    double totalIncome = completedBookings.stream()
	        .mapToDouble(Booking::getTotalPrice)
	        .sum();

	    Map<String, Double> incomeByMonth = completedBookings.stream()
	        .collect(Collectors.groupingBy(
	            b -> b.getEndDate().getMonth().toString(),
	            TreeMap::new, // để các tháng sắp xếp theo thứ tự
	            Collectors.summingDouble(Booking::getTotalPrice)
	        ));
	    
	    Map<String, Double> incomeByServiceType = completedBookings.stream()
	    		.collect(Collectors.groupingBy(
	    	            b -> b.getGuideService().getType().toString(),
	    	            TreeMap::new,
	    	            Collectors.summingDouble(Booking::getTotalPrice)
	    	        ));

	    Map<String, Object> response = new HashMap<>();
	    response.put("totalIncome", totalIncome);
	    response.put("monthlyIncome", incomeByMonth);
	    response.put("bookings", completedBookings);
	    response.put("incomeByServiceType", incomeByServiceType);

	    return response;
	}
}
