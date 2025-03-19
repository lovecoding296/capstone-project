package funix.tgcp.trip;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.trip.image.TripImage;
import funix.tgcp.trip.itinerary.Activity;
import funix.tgcp.trip.itinerary.Itinerary;
import funix.tgcp.user.User;
import funix.tgcp.user.UserService;
import funix.tgcp.util.FileUploadHelper;

@RestController
@RequestMapping("/api/trips")
public class TripRestController {

	@Autowired
	private TripService tripService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private FileUploadHelper fileUploadHelper;
	
	@PostMapping(value = "/new")
	public ResponseEntity<String> createTrip(
			@RequestParam("title") String title,
			@RequestParam("description") String description, 
			@RequestParam("maxParticipants") int maxParticipants,
			@RequestParam("category") TripCategory category, 
			@RequestPart("files") List<MultipartFile> files,
			@RequestParam("itineraries") String itinerariesJson) {

		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		Long userId = userDetails.getId();
		
		
		// Extract JWT token from Authorization header
//	    String token = authHeader.substring(7);
//	    Long userId = jwtUtil.extractUserId(token); // Extract userId from JWT token

	    Optional<User> loggedInUser = userService.findById(userId);
		
		
		Trip trip = new Trip();
		trip.setCreator(loggedInUser.get());
		trip.setCategory(category);
		trip.setMaxParticipants(maxParticipants);
		trip.setDescription(description);
		trip.setTitle(title);
		
		for (MultipartFile file : files) {
			System.out.println("Uploaded file: " + file.getOriginalFilename());
			try {
				TripImage image = new TripImage();
				image.setUrl(fileUploadHelper.uploadFile(file));
				image.setTrip(trip);
				trip.getImages().add(image);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (Itinerary itinerary : trip.getItineraries()) {
			System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxx");
			itinerary.setTrip(trip);
			for (Activity activity : itinerary.getActivities()) {
				activity.setItinerary(itinerary);
			}
		}
		tripService.createTrip(trip, loggedInUser.get().getId());

		return ResponseEntity.status(HttpStatus.CREATED).body("Ok");
	}

}
