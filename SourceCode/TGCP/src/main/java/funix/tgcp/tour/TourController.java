package funix.tgcp.tour;

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
import funix.tgcp.guide.request.GuideRequestController;
import funix.tgcp.tour.image.TourImage;
import funix.tgcp.tour.image.TourImageRepository;
import funix.tgcp.tour.image.TourImageService;
import funix.tgcp.tour.itinerary.Activity;
import funix.tgcp.tour.itinerary.Itinerary;
import funix.tgcp.tour.itinerary.ItineraryService;
import funix.tgcp.user.User;
import funix.tgcp.user.UserService;
import funix.tgcp.util.FileUploadHelper;
import funix.tgcp.util.LogHelper;

@RestController
public class TourController {
	
	private static final LogHelper logger = new LogHelper(TourController.class);

	@Autowired
	private TourService tourService;

	@Autowired
	private UserService userService;

	@Autowired
	private TourImageService tourImageService;

	@Autowired
	private FileUploadHelper fileUploadHelper;

	@Autowired
	private ItineraryService itineraryService;
	
	@GetMapping("/api/tours")
	public ResponseEntity<List<Tour>> findByCreatorId() {
		logger.info("");
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tour> tours;
		if (principal instanceof CustomUserDetails) {
		    CustomUserDetails userDetails = (CustomUserDetails) principal;
			Long userId = userDetails.getId();
			
			if(userDetails.isAdmin()) {
				logger.info("admin -> get all tours");
				tours = tourService.findAll();
			} else {
				logger.info("get all tours by user id " + userId);
				tours = tourService.findByCreatorId(userId);
			}			
		}
		else {
			logger.info("not logged in -> get all tours");
			tours = tourService.findAll();
		}
		return  ResponseEntity.ok(tours);
	}


	@GetMapping("/api/tours/{id}")
	public ResponseEntity<Tour> getTour(@PathVariable Long id) {
		Tour tour = tourService.findById(id);
		return tour != null ? ResponseEntity.ok(tour) : ResponseEntity.notFound().build();
	}

	@PostMapping("/api/tours")
	public ResponseEntity<?> createTour(@RequestPart("tour") Tour tour,
			@RequestPart("files") List<MultipartFile> files) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		Long userId = userDetails.getId();

		System.out.println("tour title " + tour.getTitle() + " " + tour.getCity());

		for (MultipartFile file : files) {
			System.out.println("Uploaded file: " + file.getOriginalFilename());
			try {
				TourImage image = new TourImage();
				image.setUrl(fileUploadHelper.uploadFile(file));
				image.setTour(tour);
				tour.getImages().add(image);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (Itinerary itinerary : tour.getItineraries()) {
			itinerary.setTour(tour);
			for (Activity activity : itinerary.getActivities()) {
				activity.setItinerary(itinerary);
			}
		}

		Tour tourSaved = tourService.createTour(tour, userId);

		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("tourId", tourSaved.getId()));

	}
	
	@PutMapping("/api/tours/{tourId}")
	public ResponseEntity<?> updateTour(@PathVariable Long tourId, @RequestPart("tour") Tour tour) {
		logger.info("");
		logger.debug("Phi " + tour.getDescription());
		Tour tourSaved = tourService.updateTour(tourId  , tour);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("tourId", tourSaved.getId()));

	}

	// tour image//
	@PostMapping("/api/tours/{tourId}/images")
	public ResponseEntity<?> uploadImages(@PathVariable Long tourId, @RequestParam("images") MultipartFile[] files) {
		tourImageService.uploadImages(tourId, files);
		return ResponseEntity.ok(Map.of("success", true));
	}

	@DeleteMapping("/api/tours/images/{imageId}")
	public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
		tourImageService.deleteById(imageId);
		return ResponseEntity.ok("Ảnh đã bị xóa");
	}

	// tour itinerary//
	@PutMapping("/api/tours/itineraries/{itineraryId}")
	public ResponseEntity<?> updateItinerary(@PathVariable Long itineraryId, @RequestBody Itinerary itineraryRequest) {
		System.out.println("updateItinerary " + itineraryId + " dayNo " + itineraryRequest.getDayNo());

		Itinerary itinerary = itineraryService.findItineraryById(itineraryId);
		if (itinerary != null) {
			itinerary.setDayNo(itineraryRequest.getDayNo());
		}
		itineraryService.updateItinerary(itinerary);
		return ResponseEntity.ok(Map.of("success", true));
	}

	@DeleteMapping("/api/tours/itineraries/{itineraryId}")
	public ResponseEntity<?> deleteItinerary(@PathVariable Long itineraryId) {
		System.out.println("deleteItinerary " + itineraryId);
		itineraryService.deleteItineraryById(itineraryId);
		return ResponseEntity.ok(Map.of("success", true));
	}

	@PostMapping("/api/tours/{tourId}/itineraries")
	public ResponseEntity<?> addItinerary(@PathVariable Long tourId, @RequestBody Itinerary itinerary) {
		Tour tour = tourService.findById(tourId);
		itinerary.setTour(tour);

		System.out.println("itinerary day No " + itinerary.getDayNo());

		for (Activity activity : itinerary.getActivities()) {
			activity.setItinerary(itinerary);
		}
		Itinerary savedItinerary = itineraryService.createItinerary(itinerary);
		return ResponseEntity.ok(Map.of("id", savedItinerary.getId()));
	}

	@PostMapping("/api/tours/itineraries/{itineraryId}/activities")
	public ResponseEntity<?> addActivity(@PathVariable Long itineraryId, @RequestBody Activity activity) {
		Itinerary itinerary = itineraryService.findItineraryById(itineraryId);

		if (itinerary != null) {
			activity.setItinerary(itinerary);
			Activity savedActivity = itineraryService.createActivity(activity);
			return ResponseEntity.ok(Map.of("id", savedActivity.getId()));
		}
		return ResponseEntity.badRequest().body("");
	}

	@PutMapping("/api/tours/itineraries/activities/{activityId}")
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
	
	
	@DeleteMapping("/api/tours/itineraries/activities/{activityId}")
	public ResponseEntity<?> deleteActivity(@PathVariable Long activityId) {
		itineraryService.deleteActivityById(activityId);
		return ResponseEntity.ok("Ảnh đã bị xóa");
	}
	
	
	/*ADMIN*/	
	@GetMapping("/api/admin/tours/pending")
	public List<Tour> getPendingTours() {
		return tourService.getPendingTours();
	}
	
	@PutMapping("/api/admin/tours/{id}/approve")
    public ResponseEntity<String> approveTour(@PathVariable Long id) {
		logger.info("");
		tourService.approveTour(id);
        return ResponseEntity.ok("Tour approved successfully.");
    }

    @PutMapping("/api/admin/tours/{id}/reject")
    public ResponseEntity<String> rejectTour(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
    	logger.info("reason " +  requestBody.get("reason"));
    	tourService.rejectTour(id, requestBody.get("reason"));
        return ResponseEntity.ok("Tour rejected successfully.");
    }
	
	

}
