package funix.tgcp.tour;

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

import funix.tgcp.tour.image.TourImage;
import funix.tgcp.tour.image.TourImageRepository;
import funix.tgcp.tour.itinerary.Itinerary;
import funix.tgcp.user.Language;
import funix.tgcp.user.User;
import funix.tgcp.util.FileUploadHelper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/tours")
public class TourController {

	@Autowired
	private TourService tourService;

	@Autowired
	private FileUploadHelper fileUploadHelper;

	@Autowired
	private TourImageRepository tourImageRepository;

	@GetMapping
	public String getAllTours() {
		return "tour/tour-list"; // Tên của view (HTML)
	}

	@GetMapping("/new")
	public String showCreateForm() {
		return "tour/tour-new"; // Form tạo chuyến đi mới
	}
	
	@GetMapping("/{id}")
	public String findById(@PathVariable Long id) {
		return "tour/tour-details"; // View cho chi tiết chuyến đi
	}

	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable Long id) {
		return "tour/tour-edit"; // Form chỉnh sửa chuyến đi
	}

//	private void deleteImageFromDatabase(String imageUrl) {
//		// Tìm và xóa TourImage từ cơ sở dữ liệu theo URL
//		TourImage imageToDelete = tourImageRepository.findByUrl(imageUrl);
//		if (imageToDelete != null) {
//			tourImageRepository.delete(imageToDelete);
//			System.out.println("Image deleted from database: " + imageUrl);
//		}
//	}
//
//	private void deleteImageFile(String imageUrl) {
//		// Logic để xóa ảnh từ hệ thống tệp
//		Path path = Paths.get(imageUrl); // Đảm bảo đường dẫn đúng
//		try {
//			Files.deleteIfExists(path); // Xóa ảnh nếu tồn tại
//			System.out.println("Image file deleted from system: " + imageUrl);
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.out.println("Error deleting image file: " + imageUrl);
//		}
//	}

//	@DeleteMapping("/{id}")
//	public String deleteTour(@PathVariable Long id) {
//		tourService.deleteTour(id);
//		return "redirect:/tours"; // Sau khi xóa xong, chuyển hướng về danh sách chuyến đi
//	}
//
//	@GetMapping("/category/{category}")
//	public String getToursByCategory(@PathVariable TourCategory category, Model model) {
//		List<Tour> tours = tourService.findByCategory(category);
//		model.addAttribute("tours", tours);
//		return "tour/list"; // View danh sách chuyến đi theo category
//	}
//
//	@GetMapping("/creator/{userId}")
//	public String findByCreatorId(@PathVariable Long userId, Model model) {
//		List<Tour> tours = tourService.findByCreatorId(userId);
//		model.addAttribute("tours", tours);
//		return "tour/list"; // View danh sách chuyến đi của người tạo
//	}
//
//	@GetMapping("/participant/{userId}")
//	public String getToursByParticipant(@PathVariable Long userId, Model model) {
//		List<Tour> tours = tourService.findByParticipantId(userId);
//		model.addAttribute("tours", tours);
//		return "tour/list"; // View danh sách chuyến đi của người tham gia
//	}
}
