package funix.tgcp.notification;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import funix.tgcp.user.User;

@Service
public class NotificationService {

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

	public List<Notification> findByUserId(Long currentUserId) {
		return notificationRepository.findByUserId(currentUserId);
	}


}
