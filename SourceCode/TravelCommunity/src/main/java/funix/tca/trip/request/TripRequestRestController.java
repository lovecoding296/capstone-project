package funix.tca.trip.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import funix.tca.appuser.AppUser;
import funix.tca.trip.Trip;
import funix.tca.trip.TripService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/trips")

public class TripRequestRestController {

	@Autowired
	private TripService tripService;

	@Autowired
	private TripRequestService tripRequestService;

	@PostMapping("/{id}/request")
	public ResponseEntity<?> requestToJoinTrip(@PathVariable Long id, HttpSession session) {
		AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");

		System.out.println("requestToJoinTrip");

		if (loggedInUser == null) {
			System.out.println("requestToJoinTrip");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Bạn cần đăng nhập."));
		}

		Optional<Trip> tripOptional = tripService.findById(id);
		if (tripOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Chuyến đi không tồn tại."));
		}

		Trip trip = tripOptional.get();

		// Kiểm tra nếu đã gửi yêu cầu trước đó
		if (tripRequestService.hasUserRequested(loggedInUser, trip)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Bạn đã gửi yêu cầu trước đó."));
		}

		TripRequest request = new TripRequest();
		request.setTrip(trip);
		request.setUser(loggedInUser);
		request.setStatus(RequestStatus.PENDING);

		tripRequestService.save(request);

		return ResponseEntity.ok(Map.of("message", "Yêu cầu tham gia đã được gửi."));
	}


}
