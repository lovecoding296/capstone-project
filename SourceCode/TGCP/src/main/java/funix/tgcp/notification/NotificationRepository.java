package funix.tgcp.notification;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import funix.tgcp.user.User;



public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByUserIdAndIsReadFalse(Long userId);

	int countByUserIdAndIsReadFalse(Long currentUserId);

	Optional<Notification> findByUserAndSourceLinkAndMessageAndIsRead(User user, String sourceLink, String message,
			boolean b);

	List<Notification> findByUserId(Long currentUserId);    
}