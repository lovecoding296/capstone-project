package funix.tgcp.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

	
	@Query("SELECT m FROM ChatMessage m " + "WHERE m.sender.id = :userId OR m.receiver.id = :userId "
			+ "ORDER BY m.timestamp DESC")
	List<ChatMessage> findAllMessagesRelatedToUser(@Param("userId") Long userId);

	// Lấy tin nhắn chưa đọc của user
	List<ChatMessage> findBySenderIdAndReceiverIdAndIsReadFalse(Long senderId, Long receiverId);
	
	@Query(value = "SELECT * FROM chat_message WHERE " +
            "(sender_id = :userA AND receiver_id = :userB) OR " +
            "(sender_id = :userB AND receiver_id = :userA) ", nativeQuery = true)
	List<ChatMessage> getMessagesBetweenUsers(@Param("userA") Long userA, @Param("userB") Long userB);

	int countByReceiverIdAndIsReadFalse(Long currentUserId);
}
