package funix.tgcp.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
	
	@Query(value = "SELECT * FROM chat_message WHERE " +
            "(sender_id = :userA AND receiver_id = :userB) OR " +
            "(sender_id = :userB AND receiver_id = :userA) ", nativeQuery = true)
	List<ChatMessage> getMessagesBetweenUsers(@Param("userA") Long userA, @Param("userB") Long userB);

	int countByReceiverIdAndIsReadFalse(Long currentUserId);
	
	
	
	@Modifying
	@Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.sender.id = :partnerId AND m.receiver.id = :currentUserId AND m.isRead = false")
	int markAsRead(@Param("partnerId") Long partnerId, @Param("currentUserId") Long currentUserId);
	
	@Query(value = """
	    SELECT cm.* FROM chat_message cm
	    INNER JOIN (
	        SELECT 
	            LEAST(sender_id, receiver_id) AS user1,
	            GREATEST(sender_id, receiver_id) AS user2,
	            MAX(id) AS max_id
	        FROM chat_message
	        WHERE sender_id = :userId OR receiver_id = :userId
	        GROUP BY LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id)
	    ) latest ON 
	        ((LEAST(cm.sender_id, cm.receiver_id) = latest.user1) AND 
	         (GREATEST(cm.sender_id, cm.receiver_id) = latest.user2) AND 
	         cm.id = latest.max_id)
	    ORDER BY cm.timestamp DESC
	""", nativeQuery = true)
	List<ChatMessage> findLatestMessagePerPartner(@Param("userId") Long userId);

}
