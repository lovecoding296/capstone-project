package funix.tgcp.trip;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.trip.image.TripImage;
import funix.tgcp.trip.image.TripImageRepository;
import funix.tgcp.trip.itinerary.Itinerary;
import funix.tgcp.user.Language;
import funix.tgcp.user.User;
import funix.tgcp.util.FileUploadHelper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/trips")
public class TripController {

	@Autowired
	private TripService tripService;

	@Autowired
	private FileUploadHelper fileUploadHelper;

	@Autowired
	private TripImageRepository tripImageRepository;

	@GetMapping
	public String getAllTrips(Model model) {
		List<Trip> trips = tripService.findAll();
		model.addAttribute("trips", trips);
		return "trip/trip-list"; // Tên của view (HTML)
	}

	@GetMapping("/{id}/details")
	public String findById(@PathVariable Long id, Model model) {
		Trip trip = tripService.findById(id);
		model.addAttribute("trip", trip);
		return "trip/trip-details"; // View cho chi tiết chuyến đi
	}

	@GetMapping("/new")
	public String showCreateForm(Model model) {
		model.addAttribute("trip", new Trip());
		model.addAttribute("categories", TripCategory.values());
		model.addAttribute("languages", Language.values());
		return "trip/trip-form"; // Form tạo chuyến đi mới
	}

	@PostMapping("/new")
	public String createTrip(@Valid @ModelAttribute Trip trip, @RequestParam("files") List<MultipartFile> files,
			HttpSession session) {
		User loggedInUser = (User) session.getAttribute("loggedInUser");
		System.out.println("create trip....");
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
		
		Set<Itinerary> itineraries = trip.getItineraries();
		System.out.println("itineraries size" + itineraries.size());
		
		System.out.println("create trip done ....");
		tripService.createTrip(trip, loggedInUser.getId());
		return "redirect:/trips"; // Sau khi tạo xong, chuyển hướng về danh sách chuyến đi
	}

	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable Long id, Model model) {
		Trip trip = tripService.findById(id);
		model.addAttribute("trip", trip);
		model.addAttribute("categories", TripCategory.values());
		model.addAttribute("languages", Language.values());
		return "trip/trip-edit-form"; // Form chỉnh sửa chuyến đi
	}

	@PostMapping("/{id}/edit")
	public String updateTrip(@PathVariable Long id, @Valid @ModelAttribute Trip trip,
			@RequestParam("files") List<MultipartFile> files, @RequestParam("imagesToDelete") String imagesToDelete,
			HttpSession session) {

		// Chuyển chuỗi imagesToDelete thành List
		List<String> imagesToDeleteList = Arrays.asList(imagesToDelete.split(";"));

		// Xử lý xóa ảnh
		for (String url : imagesToDeleteList) {
			if (!url.isEmpty()) {

				System.out.println("Deleting image: " + url);
				// Xóa ảnh từ cơ sở dữ liệu
				deleteImageFromDatabase(url);

				// Nếu ảnh lưu trên file system, xóa ảnh từ hệ thống
				deleteImageFile(url); // Implement this method if needed
			}
		}
		System.out.println("files size " + files.size());
		for (MultipartFile file : files) {
			System.out.println("Uploaded file: " + file.getOriginalFilename());
			try {
				if (file.getOriginalFilename().isBlank() == false) {
					TripImage tripImage = new TripImage();
					tripImage.setUrl(fileUploadHelper.uploadFile(file));
					tripImage.setTrip(trip);
					trip.getImages().add(tripImage);
					tripImageRepository.save(tripImage);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		tripService.updateTrip(id, trip);

		return "redirect:/trips"; // Sau khi sửa xong, chuyển hướng về danh sách chuyến đi
	}

	private void deleteImageFromDatabase(String imageUrl) {
		// Tìm và xóa TripImage từ cơ sở dữ liệu theo URL
		TripImage imageToDelete = tripImageRepository.findByUrl(imageUrl);
		if (imageToDelete != null) {
			tripImageRepository.delete(imageToDelete);
			System.out.println("Image deleted from database: " + imageUrl);
		}
	}

	private void deleteImageFile(String imageUrl) {
		// Logic để xóa ảnh từ hệ thống tệp
		Path path = Paths.get(imageUrl); // Đảm bảo đường dẫn đúng
		try {
			Files.deleteIfExists(path); // Xóa ảnh nếu tồn tại
			System.out.println("Image file deleted from system: " + imageUrl);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error deleting image file: " + imageUrl);
		}
	}

	@DeleteMapping("/{id}")
	public String deleteTrip(@PathVariable Long id) {
		tripService.deleteTrip(id);
		return "redirect:/trips"; // Sau khi xóa xong, chuyển hướng về danh sách chuyến đi
	}

	@GetMapping("/category/{category}")
	public String getTripsByCategory(@PathVariable TripCategory category, Model model) {
		List<Trip> trips = tripService.findByCategory(category);
		model.addAttribute("trips", trips);
		return "trip/list"; // View danh sách chuyến đi theo category
	}

	@GetMapping("/creator/{userId}")
	public String findByCreatorId(@PathVariable Long userId, Model model) {
		List<Trip> trips = tripService.findByCreatorId(userId);
		model.addAttribute("trips", trips);
		return "trip/list"; // View danh sách chuyến đi của người tạo
	}

	@GetMapping("/participant/{userId}")
	public String getTripsByParticipant(@PathVariable Long userId, Model model) {
		List<Trip> trips = tripService.findByParticipantId(userId);
		model.addAttribute("trips", trips);
		return "trip/list"; // View danh sách chuyến đi của người tham gia
	}
}
