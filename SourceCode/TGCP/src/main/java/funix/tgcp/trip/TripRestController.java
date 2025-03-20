package funix.tgcp.trip;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.trip.image.TripImage;
import funix.tgcp.trip.image.TripImageRepository;
import funix.tgcp.trip.image.TripImageService;
import funix.tgcp.trip.itinerary.Activity;
import funix.tgcp.trip.itinerary.Itinerary;
import funix.tgcp.trip.itinerary.ItineraryService;
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
	private TripImageService tripImageService;

	@Autowired
	private FileUploadHelper fileUploadHelper;

	@Autowired
	private ItineraryService itineraryService;

	@GetMapping("/{id}")
	public ResponseEntity<Trip> getTrip(@PathVariable Long id) {
		Trip trip = tripService.findById(id);
		return trip != null ? ResponseEntity.ok(trip) : ResponseEntity.notFound().build();
	}

	@PostMapping
	public ResponseEntity<?> createTrip(@RequestPart("trip") Trip trip,
			@RequestPart("files") List<MultipartFile> files) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		Long userId = userDetails.getId();

		System.out.println("trip title " + trip.getTitle() + " " + trip.getCity());

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
			itinerary.setTrip(trip);
			for (Activity activity : itinerary.getActivities()) {
				activity.setItinerary(itinerary);
			}
		}

		Trip tripSaved = tripService.createTrip(trip, userId);

		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("tripId", tripSaved.getId()));

	}

	// trip image//
	@PostMapping("/{tripId}/images")
	public ResponseEntity<?> uploadImages(@PathVariable Long tripId, @RequestParam("images") MultipartFile[] files) {
		tripImageService.uploadImages(tripId, files);
		return ResponseEntity.ok(Map.of("success", true));
	}

	@DeleteMapping("/images/{imageId}")
	public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
		tripImageService.deleteById(imageId);
		return ResponseEntity.ok("Ảnh đã bị xóa");
	}

	// trip itinerary//
	@PutMapping("/itineraries/{itineraryId}")
	public ResponseEntity<?> updateItinerary(@PathVariable Long itineraryId, @RequestBody Itinerary itineraryRequest) {
		System.out.println("updateItinerary " + itineraryId + " dayNo " + itineraryRequest.getDayNo());

		Itinerary itinerary = itineraryService.findItineraryById(itineraryId);
		if (itinerary != null) {
			itinerary.setDayNo(itineraryRequest.getDayNo());
		}
		itineraryService.updateItinerary(itinerary);
		return ResponseEntity.ok(Map.of("success", true));
	}

	@DeleteMapping("/itineraries/{itineraryId}")
	public ResponseEntity<?> deleteItinerary(@PathVariable Long itineraryId) {
		System.out.println("deleteItinerary " + itineraryId);
		itineraryService.deleteItineraryById(itineraryId);
		return ResponseEntity.ok(Map.of("success", true));
	}

	@PostMapping("/{tripId}/itineraries")
	public ResponseEntity<?> addItinerary(@PathVariable Long tripId, @RequestBody Itinerary itinerary) {
		Trip trip = tripService.findById(tripId);
		itinerary.setTrip(trip);

		System.out.println("itinerary day No " + itinerary.getDayNo());

		for (Activity activity : itinerary.getActivities()) {
			activity.setItinerary(itinerary);
		}
		Itinerary savedItinerary = itineraryService.createItinerary(itinerary);
		return ResponseEntity.ok(Map.of("id", savedItinerary.getId()));
	}

	@PostMapping("/itineraries/{itineraryId}/activities")
	public ResponseEntity<?> addActivity(@PathVariable Long itineraryId, @RequestBody Activity activity) {
		Itinerary itinerary = itineraryService.findItineraryById(itineraryId);

		if (itinerary != null) {
			activity.setItinerary(itinerary);
			Activity savedActivity = itineraryService.createActivity(activity);
			return ResponseEntity.ok(Map.of("id", savedActivity.getId()));
		}
		return ResponseEntity.badRequest().body("");
	}

	@PutMapping("/itineraries/activities/{activityId}")
	public ResponseEntity<?> updateActivity(@PathVariable Long activityId, @RequestBody Map<String, Object> updates) {
		System.out.println("id " + activityId);
		Activity activity = itineraryService.findActivityById(activityId);
		
		if (activity != null) {
			System.out.println("updateActivity... " + updates.size());
			updates.forEach((key, value) -> {
				switch (key) {
				case "title":
					System.out.println("update title " + value.toString());
					activity.setTitle(value.toString());
					break;
				case "description":
					System.out.println("update description " + value.toString());
					activity.setDescription(value.toString());
					break;
				default:
					System.out.println("Invalid field: " + key);
				}
			});

			itineraryService.updateActivity(activity);
		}
		return ResponseEntity.ok(Map.of("success", true));
	}
	
	
	@DeleteMapping("/itineraries/activities/{activityId}")
	public ResponseEntity<?> deleteActivity(@PathVariable Long activityId) {
		itineraryService.deleteActivityById(activityId);
		return ResponseEntity.ok("Ảnh đã bị xóa");
	}

}
