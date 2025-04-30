package funix.tgcp.chat;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.user.User;
import funix.tgcp.user.UserRepository;
import funix.tgcp.util.LogHelper;

@Controller
public class ChatController {

	private static final LogHelper logger = new LogHelper(ChatController.class);

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private ChatMessageRepository messageRepository;

	@Autowired
	private ChatMessageService chatMessageService;

	@Autowired
	private UserRepository userRepository;

	@MessageMapping("/chat.send") // Khi người dùng gửi tin nhắn
	public void sendMessage(@Payload ChatMessage message, Principal principal) {

		if (principal == null)
			return;

		Optional<User> userOp = userRepository.findByEmail(principal.getName());
		if (userOp.isPresent()) {
			User sender = userOp.get();

			message.setTimestamp(LocalDateTime.now());

			if (message.getReceiver().getId() == sender.getId()) {
				message.setRead(true);
			}


			message.setSender(sender);
			ChatMessage savedMessage = messageRepository.save(message);
			
			
			logger.info("sender " + savedMessage.getSender().getEmail());
			logger.info("getReceiver " + savedMessage.getReceiver().getEmail());
						
			messagingTemplate.convertAndSendToUser(savedMessage.getReceiver().getEmail(), "/queue/notification", Map.of("type", "NEW_MESSAGE"));			
			messagingTemplate.convertAndSendToUser(savedMessage.getSender().getEmail(), "/queue/chat", savedMessage);
			messagingTemplate.convertAndSendToUser(savedMessage.getReceiver().getEmail(), "/queue/chat", savedMessage);
			
			if (!message.isRead()) {
				
			}
		}

	

	}

	// Endpoint để đánh dấu tin nhắn là đã đọc
	@MessageMapping("/chat.read")
	public void markMessageAsRead(@Payload Long partnerId, Principal principal) {		
		logger.info("markMessageAsRead");
		Optional<User> userOp = userRepository.findByEmail(principal.getName());

		if (userOp.isPresent()) {
			User user = userOp.get();

			chatMessageService.markMessagesAsRead(partnerId, user.getId());

			messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/notification",
					Map.of("type", "REFRESH_MESSAGE_BADGE"));
		}
		
	}
}
