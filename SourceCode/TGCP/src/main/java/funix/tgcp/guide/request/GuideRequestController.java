package funix.tgcp.guide.request;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.util.LogHelper;

@RestController
public class GuideRequestController {
	
	private static final LogHelper logger = new LogHelper(GuideRequestController.class);

	
    @Autowired
    private GuideRequestService guideRequestService;
    
    @GetMapping("/api/guide-requests/status")
    public ResponseEntity<?> getRegistrationStatus() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            Long userId = userDetails.getId();

            GuideRequest guideRequest = guideRequestService.findByUserId(userId);            
            if (guideRequest != null) {
                return ResponseEntity.ok(guideRequest); // Trả về đối tượng GuideRequest
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy đơn đăng ký.");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User chưa đăng nhập.");
    }

    @PostMapping("/api/guide-requests/register")
    public ResponseEntity<String> registerGuide(
            @RequestParam("guideLicenseFile") MultipartFile guideLicenseFile,
            @RequestParam("guideLicense") String guideLicense,
            @RequestParam("experience") String experience) {
    	
    	logger.info("");
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof CustomUserDetails) {
		    CustomUserDetails userDetails = (CustomUserDetails) principal;
			Long userId = userDetails.getId();
			
	        try {
	        	System.out.println("GuideRequestController guideRequestService");
	        	logger.info("userId " + userId);
	            guideRequestService.registerGuide(userId, guideLicenseFile, 
	            		guideLicense, experience);
	            return ResponseEntity.ok("Đăng ký hướng dẫn viên thành công!");
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đăng ký thất bại: " + e.getMessage());
	        }
		}
		else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User chua dang nhap");
		}
    }  
    
    @PutMapping("/api/guide-requests/register")
    public ResponseEntity<String> updateGuideRequest(
            @RequestParam(value = "guideLicenseFile" , required = false) MultipartFile guideLicenseFile,
            @RequestParam("guideLicense") String guideLicense,
            @RequestParam("experience") String experience) {
        
    	logger.info("");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            Long userId = userDetails.getId();
            
            try {
                logger.info("Cập nhật đăng ký hướng dẫn viên cho userId: " + userId);
                guideRequestService.updateGuideRequest(userId, guideLicenseFile, guideLicense, experience);
                return ResponseEntity.ok("Cập nhật đăng ký hướng dẫn viên thành công!");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cập nhật thất bại: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User chưa đăng nhập");
        }
    }

    
    
    
    
    /*ADMIN*/    
    @GetMapping("/api/admin/guide-requests")
	public List<GuideRequest> getAllGuideRequests() {
		return guideRequestService.getAllGuideRequests();
	}

	@PutMapping("/api/admin/guide-requests/{id}/approve")
	public ResponseEntity<String> approveGuide(@PathVariable Long id) {
		boolean success = guideRequestService.approveGuide(id);
		return success ? ResponseEntity.ok("Đã phê duyệt hướng dẫn viên!")
				: ResponseEntity.badRequest().body("Không tìm thấy yêu cầu!");
	}

	@PutMapping("/api/admin/guide-requests/{id}/reject")
	public ResponseEntity<String> rejectGuide(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
		boolean success = guideRequestService.rejectGuide(id, requestBody.get("reason"));
		return success ? ResponseEntity.ok("Đã từ chối yêu cầu!")
				: ResponseEntity.badRequest().body("Không tìm thấy yêu cầu!");
	}
   

}
