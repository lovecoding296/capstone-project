package funix.tgcp.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatMessageService {
	
	@Autowired
	ChatMessageRepository chatRepo;
	

	@Transactional
	public void markMessagesAsRead(Long partnerId, Long currentUserId) {
        chatRepo.markAsRead(partnerId, currentUserId);        
    }
	
	public void markMessageAsRead(Long messageId) {
        
        chatRepo.findById(messageId).ifPresent(mes -> {
        	mes.setRead(true);
        	chatRepo.save(mes);
        });        
    }


	public int countUnreadMessages(Long currentUserId) {
		return chatRepo.countByReceiverIdAndIsReadFalse(currentUserId);
	}
}
