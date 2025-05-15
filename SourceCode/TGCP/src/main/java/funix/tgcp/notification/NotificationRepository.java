package funix.tgcp.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;



public interface NotificationRepository extends JpaRepository<Notification, Long> {

	int countByUserIdAndIsReadFalse(Long currentUserId);

	List<Notification> findByUserIdOrderByCreatedAtDesc(Long currentUserId);    
}