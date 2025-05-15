package funix.tgcp.chat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import funix.tgcp.user.User;
import funix.tgcp.user.UserService;

@Service
public class ChatMessageService {
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	ChatMessageRepository chatRepo;

	@Autowired
	private UserService userService;
	
	public List<ChatMessage> getMessagesBetweenUsers(Long currentUserId, Long partnerId) {
        return chatRepo.getMessagesBetweenUsers(currentUserId, partnerId);
    }

    public List<Map<String, Object>> findLatestMessagePerPartner(Long currentUserId) {
        List<ChatMessage> latestMessages = chatRepo.findLatestMessagePerPartner(currentUserId);

        List<Map<String, Object>> result = new ArrayList<>();

        for (ChatMessage msg : latestMessages) {
            Long partnerId = msg.getSender().getId().equals(currentUserId)
                    ? msg.getReceiver().getId()
                    : msg.getSender().getId();

            User partner = msg.getSender().getId().equals(currentUserId)
                    ? msg.getReceiver()
                    : msg.getSender();

            Map<String, Object> item = new HashMap<>();
            item.put("partnerId", partnerId);
            item.put("fullName", partner.getFullName());
            item.put("email", partner.getEmail());
            item.put("avatarUrl", partner.getAvatarUrl());
            item.put("lastMessage", msg.getContent());
            item.put("read", msg.getSender().getId().equals(currentUserId) || msg.isRead());
            item.put("timestamp", msg.getTimestamp());

            result.add(item);
        }

        return result;
    }

	@Transactional
	public void markMessagesAsRead(Long partnerId, Long currentUserId) {
        chatRepo.markAsRead(partnerId, currentUserId);        
    }

	public int countUnreadMessages(Long currentUserId) {
		return chatRepo.countByReceiverIdAndIsReadFalse(currentUserId);
	}

	public ChatMessage save(ChatMessage message) {
		return chatRepo.save(message);
	}

	public void processAndSendMessage(ChatMessage message, String senderEmail) {
		Optional<User> userOp = userService.findByEmail(senderEmail);
		if (userOp.isEmpty()) return;

		User sender = userOp.get();
		message.setSender(sender);
		message.setTimestamp(LocalDateTime.now());

		if (message.getReceiver().getId().equals(sender.getId())) {
			message.setRead(true); // gửi cho chính mình thì tự đánh dấu là đã đọc
		}

		ChatMessage savedMessage = chatRepo.save(message);

		messagingTemplate.convertAndSendToUser(savedMessage.getReceiver().getEmail(), "/queue/notification",
				Map.of("type", "NEW_MESSAGE"));

		messagingTemplate.convertAndSendToUser(savedMessage.getSender().getEmail(), "/queue/chat", savedMessage);
		messagingTemplate.convertAndSendToUser(savedMessage.getReceiver().getEmail(), "/queue/chat", savedMessage);
	}

	public void markAndNotifyReadMessages(Long partnerId, String currentUserEmail) {
		Optional<User> userOp = userService.findByEmail(currentUserEmail);
		if (userOp.isEmpty()) return;

		User user = userOp.get();
		markMessagesAsRead(partnerId, user.getId());

		messagingTemplate.convertAndSendToUser(currentUserEmail, "/queue/notification",
				Map.of("type", "REFRESH_MESSAGE_BADGE"));
	}
}
