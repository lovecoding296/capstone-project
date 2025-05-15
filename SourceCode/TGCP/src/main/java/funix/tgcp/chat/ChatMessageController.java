package funix.tgcp.chat;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class ChatMessageController {

	@Autowired
	private ChatMessageService chatMessageService;

	@MessageMapping("/chat.send")
	public void sendMessage(@Payload ChatMessage message, Principal principal) {
		if (principal != null) {
			chatMessageService.processAndSendMessage(message, principal.getName());
		}
	}

	@MessageMapping("/chat.read")
	public void markMessageAsRead(@Payload Long partnerId, Principal principal) {
		if (principal != null) {
			chatMessageService.markAndNotifyReadMessages(partnerId, principal.getName());
		}
	}
}

