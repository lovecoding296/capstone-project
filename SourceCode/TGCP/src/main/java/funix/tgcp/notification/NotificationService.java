package funix.tgcp.notification;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import funix.tgcp.booking.BookingService;
import funix.tgcp.user.Role;
import funix.tgcp.user.User;
import funix.tgcp.user.UserRepository;
import funix.tgcp.util.LogHelper;

@Service
public class NotificationService {

	private static final LogHelper logger = new LogHelper(NotificationService.class);

	@Autowired
	NotificationRepository notiRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	public List<Notification> findByUserIdAndIsReadFalse(Long currentUser) {
		return notiRepo.findByUserIdAndIsReadFalse(currentUser);
	}

	public void markNotificationsAsRead(Long notiId) {

		notiRepo.findById(notiId).ifPresent(noti -> {
			noti.setRead(true);
			notiRepo.save(noti);
		});	

	}

	public int countUnreadNotifications(Long currentUserId) {
		return notiRepo.countByUserIdAndIsReadFalse(currentUserId);
	}

	public void save(Notification notify) {
		notiRepo.save(notify);
	}

	public List<Notification> findByUserIdOrderByCreatedAtDesc(Long currentUserId) {
		return notiRepo.findByUserIdOrderByCreatedAtDesc(currentUserId);
	}
	
	
	public void sendNotification(User receiver, String message, String sourceLink) {
	    Notification notify = new Notification();
	    notify.setUser(receiver);
	    notify.setMessage(message);
	    notify.setSourceLink(sourceLink);
	    save(notify);
	    
	    userRepo.findById(receiver.getId()).ifPresent(user -> {
	    	messagingTemplate.convertAndSendToUser(user.getEmail(), "/queue/notification",
					Map.of("type", "NEW_NOTIFICATION"));
	    });
	    
		

	    logger.info("Notification: {}", message);
	}

	public void sendNotificationToAdmin(String message, String sourceLink) {
		logger.info("");
		List<User> admins = userRepo.findByRole(Role.ROLE_ADMIN);
		
		for(User admin : admins) {
			sendNotification(admin, message, sourceLink);
		}		
	}

}
