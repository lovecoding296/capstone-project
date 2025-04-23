package funix.tgcp.notification;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import funix.tgcp.chat.ChatMessage;
import funix.tgcp.chat.ChatMessageController;
import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.user.User;
import funix.tgcp.util.LogHelper;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	
	private static final LogHelper logger = new LogHelper(NotificationController.class);

	
	@Autowired
	private NotificationService notificationService;

	@GetMapping
	public ResponseEntity<?> viewNotifications() {
		CustomUserDetails userDetails = CustomUserDetails.getCurrentUserDetails();
    	
		Long currentUserId = (long)1;
		if(userDetails != null ) {
			currentUserId = userDetails.getId();
    		
    	} 
    	    	
	    List<Notification> list = notificationService.findByUserIdOrderByCreatedAtDesc(currentUserId);
	    return ResponseEntity.ok(list);
	}
	
	
	@PostMapping("/mark-read")
    public ResponseEntity<?> markNotificationsAsRead(@RequestBody Notification request) {
		
		logger.info("" + request.getId());
        
        notificationService.markNotificationsAsRead(request.getId());
        return ResponseEntity.ok().body("Đã đánh dấu là đã đọc");
    }
    
    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadNotificationsCount() {
    	Long currentUserId = 1L;
        CustomUserDetails userDetails = CustomUserDetails.getCurrentUserDetails();
        if (userDetails != null) {
        	currentUserId = userDetails.getId();
        }
        
        
        int count = notificationService.countUnreadNotifications(currentUserId);
        return ResponseEntity.ok(count);
    }
}
