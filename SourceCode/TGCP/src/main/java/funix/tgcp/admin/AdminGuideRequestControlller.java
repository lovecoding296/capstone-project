package funix.tgcp.admin;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import funix.tgcp.guide.request.GuideRequest;
import funix.tgcp.guide.request.GuideRequestService;


@RestController
@RequestMapping("/api/admin/guide-requests")
public class AdminGuideRequestControlller {

	@Autowired
	private GuideRequestService guideRequestService;

	@GetMapping
	public List<GuideRequest> getAllGuideRequests() {
		return guideRequestService.getAllGuideRequests();
	}

	@PutMapping("/{id}/approve")
	public ResponseEntity<String> approveGuide(@PathVariable Long id) {
		boolean success = guideRequestService.approveGuide(id);
		return success ? ResponseEntity.ok("Đã phê duyệt hướng dẫn viên!")
				: ResponseEntity.badRequest().body("Không tìm thấy yêu cầu!");
	}

	@PutMapping("/{id}/reject")
	public ResponseEntity<String> rejectGuide(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
		boolean success = guideRequestService.rejectGuide(id, requestBody.get("reason"));
		return success ? ResponseEntity.ok("Đã từ chối yêu cầu!")
				: ResponseEntity.badRequest().body("Không tìm thấy yêu cầu!");
	}
}
