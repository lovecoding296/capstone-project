package funix.tgcp.notification;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import funix.tgcp.booking.BookingService;
import funix.tgcp.user.User;
import funix.tgcp.util.LogHelper;

@Service
public class NotificationService {

	private static final LogHelper logger = new LogHelper(NotificationService.class);

	@Autowired
	NotificationRepository notificationRepository;

	public List<Notification> findByUserIdAndIsReadFalse(Long currentUser) {
		return notificationRepository.findByUserIdAndIsReadFalse(currentUser);
	}

	public void markNotificationsAsRead(Long notiId) {

		notificationRepository.findById(notiId).ifPresent(noti -> {
			noti.setRead(true);
			notificationRepository.save(noti);
		});	

	}

	public int countUnreadNotifications(Long currentUserId) {
		return notificationRepository.countByUserIdAndIsReadFalse(currentUserId);
	}

	public void save(Notification notify) {
	    boolean exists = notificationRepository
	        .findByUserAndSourceLinkAndMessageAndIsRead(
	            notify.getUser(),
	            notify.getSourceLink(),
	            notify.getMessage(),
	            false
	        ).isPresent();

	    if (!exists) {
	        notificationRepository.save(notify);
	    }
	}

	public List<Notification> findByUserIdOrderByCreatedAtDesc(Long currentUserId) {
		return notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUserId);
	}
	
	
	public void sendNotification(User receiver, String message, String sourceLink) {
	    Notification notify = new Notification();
	    notify.setUser(receiver);
	    notify.setMessage(message);
	    notify.setSourceLink(sourceLink);
	    save(notify);

	    logger.info("Notification: {}", message);
	}

}
