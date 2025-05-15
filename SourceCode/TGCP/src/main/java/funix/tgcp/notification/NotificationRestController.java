package funix.tgcp.notification;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import funix.tgcp.chat.ChatMessage;
import funix.tgcp.chat.ChatMessageRestController;
import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.user.User;
import funix.tgcp.util.LogHelper;

@RestController
@RequestMapping("/api/notifications")
public class NotificationRestController {

	
	private static final LogHelper logger = new LogHelper(NotificationRestController.class);

	
	@Autowired
	private NotificationService notificationService;

	@GetMapping
	public ResponseEntity<?> viewNotifications(@AuthenticationPrincipal CustomUserDetails auth) {
    	
		Long currentUserId = (long)1;
		if(auth != null ) {
			currentUserId = auth.getId();
    		
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
    public ResponseEntity<Integer> getUnreadNotificationsCount(@AuthenticationPrincipal CustomUserDetails auth) {
    	Long currentUserId = 1L;
        if (auth != null) {
        	currentUserId = auth.getId();
        }
        
        
        int count = notificationService.countUnreadNotifications(currentUserId);
        return ResponseEntity.ok(count);
    }
}
