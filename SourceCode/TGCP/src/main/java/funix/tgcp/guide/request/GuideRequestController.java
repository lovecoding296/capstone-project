package funix.tgcp.guide.request;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/guide-requests")
public class GuideRequestController {
    @Autowired
    private GuideRequestService guideRequestService;
    
   

    @PostMapping("/register")
    public ResponseEntity<String> registerGuide(
            @RequestParam("guideLicenseFile") MultipartFile guideLicenseFile,
            @RequestParam("guideLicense") String guideLicense,
            @RequestParam("experience") String experience) {

    	Long userId = (long) 2;
        try {
            guideRequestService.registerGuide(userId, guideLicenseFile, 
            		guideLicense, experience);
            return ResponseEntity.ok("Đăng ký hướng dẫn viên thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đăng ký thất bại: " + e.getMessage());
        }
    }
    
   

}
