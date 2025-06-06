package funix.tgcp.guide.request;

import java.util.List;
import java.util.Map;
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
import funix.tgcp.user.Role;
import funix.tgcp.user.User;
import funix.tgcp.util.LogHelper;
import jakarta.servlet.http.HttpSession;

@RestController
public class GuideRequestRestController {
	
	private static final LogHelper logger = new LogHelper(GuideRequestRestController.class);

	
    @Autowired
    private GuideRequestService guideRequestService;
    
    @GetMapping("/api/guide-requests/status")
    public ResponseEntity<?> getRegistrationStatus() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails auth = (CustomUserDetails) principal;
            Long userId = auth.getId();
            
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
    public ResponseEntity<?> registerGuide(
    		@RequestParam MultipartFile cccdFile,
            @RequestParam MultipartFile certificateFile,
            @RequestParam String certificateNumber,
            @RequestParam String experience,
            @RequestParam boolean isInternationalGuide, 
            @RequestParam boolean isLocalGuide) {
    	
    	logger.info("");
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof CustomUserDetails) {
		    CustomUserDetails auth = (CustomUserDetails) principal;
			Long userId = auth.getId();
			
	        try {
	        	System.out.println("GuideRequestController guideRequestService");
	        	logger.info("userId " + userId);
	        	GuideRequest guideRequest = guideRequestService.registerGuide(userId,cccdFile, certificateFile, 
	        			certificateNumber, experience,isInternationalGuide,isLocalGuide);
	            logger.info("return ok");
	            return ResponseEntity.status(HttpStatus.OK).body(guideRequest);
	        } catch (Exception e) {
	        	logger.info("return INTERNAL_SERVER_ERROR");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", ""));
	        }
		}
		else {
			logger.info("return UNAUTHORIZED");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", ""));
		}
    }  
    
    @PutMapping("/api/guide-requests/register")
    public ResponseEntity<?> updateGuideRequest(
    		@RequestParam(required = false) MultipartFile cccdFile,
            @RequestParam(required = false) MultipartFile certificateFile,
            @RequestParam String certificateNumber, 
            @RequestParam String experience,
            @RequestParam boolean isLocalGuide,            
            @RequestParam boolean isInternationalGuide) {
        
    	logger.info("");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails auth = (CustomUserDetails) principal;
            Long userId = auth.getId();
            
            try {
            	GuideRequest guideRequest = guideRequestService.updateGuideRequest(userId, cccdFile, certificateFile, certificateNumber, experience, isLocalGuide, isInternationalGuide);
                if(guideRequest == null) {
                	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "NOT_FOUND"));
                }
            	return ResponseEntity.ok(guideRequest);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "INTERNAL_SERVER_ERROR"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "UNAUTHORIZED"));
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
        if (success) {            
            return ResponseEntity.ok("Guide approved successfully!");
        } else {
            return ResponseEntity.badRequest().body("An error occurred, please try again later.");
        }
    }

    @PutMapping("/api/admin/guide-requests/{id}/reject")
    public ResponseEntity<String> rejectGuide(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        boolean success = guideRequestService.rejectGuide(id, requestBody.get("reason"));
        return success 
            ? ResponseEntity.ok("Guide request rejected successfully!") 
            : ResponseEntity.badRequest().body("An error occurred, please try again later.");
    }

   

}
