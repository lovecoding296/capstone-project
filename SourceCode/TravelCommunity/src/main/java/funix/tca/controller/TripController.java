package funix.tca.controller;

import funix.tca.entity.AppUser;
import funix.tca.entity.Review;
import funix.tca.entity.Trip;
import funix.tca.entity.TripRequest;
import funix.tca.enums.RequestStatus;
import funix.tca.service.AppUserService;
import funix.tca.service.ReviewService;
import funix.tca.service.TripRequestService;
import funix.tca.service.TripService;
import funix.tca.util.FileUploadHelper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/trips")
public class TripController {

	@Autowired
	private TripService tripService;

	@Autowired
	private AppUserService appUserService;

	@Autowired
	private TripRequestService tripRequestService;
	
	@Autowired
	private ReviewService reviewService;

	// Danh sách các chuyến đi
	@GetMapping
	public String listTrips(Model model) {
		List<Trip> trips = tripService.findAll();
		model.addAttribute("trips", trips);
		return "trip/trip-list";
	}

	// Xem chi tiết chuyến đi
	@GetMapping("/{id}/details")
	public String tripById(@PathVariable Long id, Model model, HttpSession session) {
		Optional<Trip> tripOptional = tripService.findById(id);
		if (tripOptional.isPresent()) {
			Trip trip = tripOptional.get();
			model.addAttribute("trip", trip);

			AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
			if (loggedInUser != null) {
				
				boolean isOwnerOrAdmin = trip.getCreator().equals(loggedInUser) || loggedInUser.isAdmin();
				boolean isParticipant = trip.getParticipants().contains(loggedInUser);
				boolean hasRequested = !isParticipant && tripRequestService.hasUserRequested(loggedInUser, trip);

				model.addAttribute("isOwnerOrAdmin", isOwnerOrAdmin);
				model.addAttribute("isParticipant", isParticipant);
				model.addAttribute("hasRequested", hasRequested);
			} else {
				model.addAttribute("isParticipant", false);
				model.addAttribute("hasRequested", false);
			}
			
			List<Review> reviews = reviewService.findByTripId(id);
			model.addAttribute("reviews", reviews);
			return "trip/trip-details";
		}
		return "error";
	}

	// Lấy các chuyến đi của người tổ chức
	@GetMapping("/creator/{creatorId}")
	public String listTripsByCreator(@PathVariable Long creatorId, Model model) {
		List<Trip> trips = tripService.findByCreatorId(creatorId);
		model.addAttribute("trips", trips);
		return "trip/trip-list"; // Trang các chuyến đi của người tổ chức
	}

	// Tạo mới một chuyến đi
	@GetMapping("/new")
	public String showCreateForm(Model model, HttpSession session) {
		AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
		}

		model.addAttribute("trip", new Trip());
		model.addAttribute("users", appUserService.findAll());
		return "trip/trip-form"; // Trang tạo mới chuyến đi
	}

	@PostMapping("/new")
	public String createTrip(@Valid @ModelAttribute Trip trip, BindingResult result, 
			@RequestParam("tripPictureFile") MultipartFile tripPictureFile, Model model, HttpSession session) {
		AppUser user = (AppUser) session.getAttribute("loggedInUser");
		if (user == null) {
			return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
		}

		if (result.hasErrors()) {
			model.addAttribute("users", appUserService.findAll());
			return "trip/trip-form"; // Trả lại trang nếu có lỗi
		}
		try {
			String tripPictureUrl = FileUploadHelper.uploadFile(tripPictureFile);
			if(tripPictureUrl != null) {
				trip.setTripPictureUrl(tripPictureUrl);
			}
		} catch (IOException e) {
			System.out.println("upload image error");
		}
		
		trip.setCreator(user);
		tripService.save(trip);
		return "redirect:/trips"; // Chuyển hướng về trang danh sách
	}

	// Cập nhật một chuyến đi
	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
		AppUser user = (AppUser) session.getAttribute("loggedInUser");
		if (user == null) {
			return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
		}

		Trip trip = tripService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid trip ID: " + id));

		// Kiểm tra quyền chỉnh sửa
		if (!trip.getCreator().getId().equals(user.getId())) {
			return "redirect:/trips/?error=unauthorized"; // Không cho phép chỉnh sửa nếu không phải chủ sở hữu
		}

		model.addAttribute("trip", trip);
		model.addAttribute("users", appUserService.findAll());
		return "trip/trip-form"; // Trang chỉnh sửa chuyến đi
	}

	@PostMapping("/{id}/edit")
	public String updateTrip(@PathVariable Long id, @Valid @ModelAttribute Trip trip, BindingResult result,
			@RequestParam("tripPictureFile") MultipartFile tripPictureFile, Model model,
			HttpSession session) {
		AppUser user = (AppUser) session.getAttribute("loggedInUser");
		if (user == null) {
			return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
		}

		// Kiểm tra quyền chỉnh sửa
		Trip existingTrip = tripService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid trip ID: " + id));
		if (!existingTrip.getCreator().getId().equals(user.getId())) {
			return "redirect:/trips/?error=unauthorized";
		}

		if (result.hasErrors()) {
			model.addAttribute("users", appUserService.findAll());
			return "trip/trip-form";
		}

		trip.setId(id);
		trip.setCreator(existingTrip.getCreator()); // Giữ nguyên creator
		
		try {
			String tripPictureUrl = FileUploadHelper.uploadFile(tripPictureFile);
			if(tripPictureUrl != null) {
				trip.setTripPictureUrl(tripPictureUrl);
			}
		} catch (IOException e) {
			System.out.println("upload image error");
		}
		tripService.save(trip);
		return "redirect:/trips/{id}/details";
	}

	// Xóa một chuyến đi
	@GetMapping("/{id}/delete")
	public String deleteTrip(@PathVariable Long id, HttpSession session) {
		AppUser user = (AppUser) session.getAttribute("loggedInUser");
		if (user == null) {
			return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
		}

		Trip trip = tripService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid trip ID: " + id));

		// Kiểm tra quyền xóa
		if (!trip.getCreator().getId().equals(user.getId())) {
			return "redirect:/trips/?error=unauthorized";
		}
		System.out.println("TripController deleteById id = " + id);
		tripService.deleteById(id);
		return "redirect:/trips/";
	}

	// Người dùng gửi yêu cầu tham gia chuyến đi
	@PostMapping("/{id}/request")
	public String requestToJoinTrip(@PathVariable Long id, HttpSession session) {
		AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			return "redirect:/login";
		}

		Optional<Trip> tripOptional = tripService.findById(id);
		if (tripOptional.isPresent()) {
			Trip trip = tripOptional.get();

			// Kiểm tra nếu đã gửi yêu cầu trước đó
			if (tripRequestService.hasUserRequested(loggedInUser, trip)) {
				return "redirect:/trips/" + id + "/details?error=already_requested";
			}

			TripRequest request = new TripRequest();
			request.setTrip(trip);
			request.setUser(loggedInUser);
			request.setStatus(RequestStatus.PENDING);

			tripRequestService.save(request);
		}
		return "redirect:/trips/" + id + "/details?success=request_sent";
	}

	@GetMapping("/{id}/manage-requests")
	public String manageTripRequests(@PathVariable("id") Long tripId, Model model, HttpSession session) {
		AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			return "redirect:/login";
		}
		Optional<Trip> tripOptional = tripService.findById(tripId);

		if (tripOptional.isPresent()) {
			Trip trip = tripOptional.get();

			// Kiểm tra xem người dùng có phải là chủ chuyến đi hoặc admin
			boolean isOwnerOrAdmin = (trip.getCreator().equals(loggedInUser) || loggedInUser.isAdmin());

			List<TripRequest> requests = tripRequestService.findByTrip(trip);
			model.addAttribute("requests", requests);
			model.addAttribute("trip", trip);
			model.addAttribute("isOwnerOrAdmin", isOwnerOrAdmin);

			return "trip/manage-trip-requests"; // Thymeleaf template
		}

		return "trip/manage-trip-requests";
	}

	@GetMapping("/manage-all-requests")
	public String manageAllTripRequests(Model model, HttpSession session) {
		AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			return "redirect:/login"; // Redirect if not logged in
		}

		// Get all trips that the logged-in user is associated with
		List<Trip> userTrips = tripService.findByCreatorId(loggedInUser.getId()); // Assuming there's a method to find
																					// trips by user
		List<TripRequest> allRequests = new ArrayList<TripRequest>();

		// Retrieve all trip requests for the user's trips
		for (Trip trip : userTrips) {
			List<TripRequest> tripRequests = tripRequestService.findByTrip(trip);
			allRequests.addAll(tripRequests); // Add requests from each trip
		}

		model.addAttribute("allRequests", allRequests); // Pass all requests to the template
		return "trip/manage-all-trip-requests"; // Thymeleaf template
	}

	@PostMapping("/{tripId}/manage-requests/{requestId}")
	public String manageRequest(@PathVariable Long tripId,
	                            @PathVariable Long requestId,
	                            @RequestParam String action, // action là 'approve' hoặc 'reject'
	                            HttpSession session,RedirectAttributes redirectAttributes) {
	    AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");
	    if (loggedInUser == null) {
	        return "redirect:/login"; // Nếu người dùng chưa đăng nhập, chuyển hướng đến trang đăng nhập
	    }

	    Optional<Trip> tripOptional = tripService.findById(tripId);
	    if (!tripOptional.isPresent()) {
	        return "error"; // Nếu chuyến đi không tồn tại, trả về lỗi
	    }

	    Trip trip = tripOptional.get();

	    // Kiểm tra quyền của người dùng: Chỉ người tạo chuyến đi hoặc admin mới có quyền duyệt yêu cầu
	    boolean isOwnerOrAdmin = trip.getCreator().equals(loggedInUser) || loggedInUser.isAdmin();
	    if (!isOwnerOrAdmin) {
	        return "redirect:/trips"; // Nếu không phải chủ chuyến đi hoặc admin, chuyển hướng đến trang danh sách chuyến đi
	    }

	    Optional<TripRequest> requestOptional = tripRequestService.findById(requestId);
	    if (!requestOptional.isPresent()) {
	        return "error"; // Nếu yêu cầu không tồn tại, trả về lỗi
	    }

	    if ("approve".equalsIgnoreCase(action)) {
	        // Xử lý approve yêu cầu tham gia
	    	boolean approved = tripRequestService.approveRequest(requestId);
			if (approved) {
				redirectAttributes.addFlashAttribute("message", "Request approved successfully.");
			} else {
				redirectAttributes.addFlashAttribute("error", "Failed to approve request.");
			}
	    } else if ("reject".equalsIgnoreCase(action)) {
	    	boolean rejected = tripRequestService.rejectRequest(requestId);
			if (rejected) {
				redirectAttributes.addFlashAttribute("message", "Request rejected successfully.");
			} else {
				redirectAttributes.addFlashAttribute("error", "Failed to reject request.");
			}
	    }

	    // Cập nhật chuyến đi
	    tripService.save(trip);

	    // Sau khi duyệt hoặc từ chối, chuyển hướng lại trang quản lý yêu cầu
	    return "redirect:/trips/manage-all-requests"; // Điều chỉnh URL phù hợp
	}


	@PostMapping("/{tripId}/requests/{requestId}/approve")
	public String approveRequest(@PathVariable("tripId") Long tripId, @PathVariable("requestId") Long requestId,
			RedirectAttributes redirectAttributes) {
		boolean approved = tripRequestService.approveRequest(requestId);
		if (approved) {
			redirectAttributes.addFlashAttribute("message", "Request approved successfully.");
		} else {
			redirectAttributes.addFlashAttribute("error", "Failed to approve request.");
		}
		return "redirect:/trips/{tripId}/manage-requests";
	}

	@PostMapping("/{tripId}/requests/{requestId}/reject")
	public String rejectRequest(@PathVariable("tripId") Long tripId, @PathVariable("requestId") Long requestId,
			RedirectAttributes redirectAttributes) {
		boolean rejected = tripRequestService.rejectRequest(requestId);
		if (rejected) {
			redirectAttributes.addFlashAttribute("message", "Request rejected successfully.");
		} else {
			redirectAttributes.addFlashAttribute("error", "Failed to reject request.");
		}
		return "redirect:/trips/{tripId}/manage-requests";
	}
	
	
	@PostMapping("/{tripId}/remove-participant/{userId}")
	public String removeParticipant(@PathVariable Long tripId,
	                                @PathVariable Long userId,
	                                HttpSession session) {
	    AppUser loggedInUser = (AppUser) session.getAttribute("loggedInUser");

	    if (loggedInUser == null) {
	        return "redirect:/login"; // Nếu người dùng chưa đăng nhập, chuyển hướng đến trang đăng nhập
	    }

	    Optional<Trip> tripOptional = tripService.findById(tripId);
	    if (!tripOptional.isPresent()) {
	        return "error"; // Nếu chuyến đi không tồn tại, trả về lỗi
	    }

	    Trip trip = tripOptional.get();

	    // Kiểm tra quyền của người dùng: Chỉ người tạo chuyến đi hoặc admin mới có quyền xóa người tham gia
	    boolean isOwnerOrAdmin = trip.getCreator().equals(loggedInUser) || loggedInUser.isAdmin();
	    if (!isOwnerOrAdmin) {
	        return "redirect:/trips"; // Nếu không phải chủ chuyến đi hoặc admin, chuyển hướng đến trang danh sách chuyến đi
	    }

	    // Kiểm tra người tham gia có tồn tại trong chuyến đi không
	    Optional<AppUser> participantOptional = trip.getParticipants().stream()
	                                                .filter(participant -> participant.getId().equals(userId))
	                                                .findFirst();
	    if (!participantOptional.isPresent()) {
	        return "redirect:/trips/{tripId}/details"; // Nếu người tham gia không tồn tại, chuyển hướng về trang chi tiết chuyến đi
	    }

	    AppUser participant = participantOptional.get();
	    
	    tripService.deleteParticipantFromTrip(participant, trip);

	    // Chuyển hướng về trang chi tiết chuyến đi
	    return "redirect:/trips/{tripId}/details";
	}

}
