package funix.tgcp.chat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {
	
	@Autowired
	ChatMessageRepository chatRepo;
	

	public void markMessagesAsRead(Long partnerId, Long currentUserId) {
        List<ChatMessage> unreadMessages = chatRepo
            .findBySenderIdAndReceiverIdAndIsReadFalse(partnerId, currentUserId);

        for (ChatMessage message : unreadMessages) {
            message.setRead(true);
        }
        
        chatRepo.saveAll(unreadMessages);
    }


	public int countUnreadMessages(Long currentUserId) {
		return chatRepo.countByReceiverIdAndIsReadFalse(currentUserId);
	}
}
