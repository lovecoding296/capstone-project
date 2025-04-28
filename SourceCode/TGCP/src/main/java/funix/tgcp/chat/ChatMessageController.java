package funix.tgcp.chat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.user.User;
import funix.tgcp.util.LogHelper;

@RestController
@RequestMapping("/api/chat")
public class ChatMessageController {
	
	private static final LogHelper logger = new LogHelper(ChatMessageController.class);


    @Autowired
    private ChatMessageRepository chatRepo;
    
    @Autowired
    private ChatMessageService chatService;
    
    // Gửi tin nhắn
    @PostMapping("/send")
    public ChatMessage sendMessage(
    		@RequestBody ChatMessage message,
    		@AuthenticationPrincipal CustomUserDetails userDetails) {
        message.setTimestamp(LocalDateTime.now());
        
        User sender = new User();
		
		if(userDetails != null) {
			sender.setId(userDetails.getId());
		} else {
			sender.setId(1l);
		}
		
		if(message.getReceiver().getId() == sender.getId()) {
			message.setRead(true);
		}
				
		message.setSender(sender);
        return chatRepo.save(message);
    }    
    
    // Lấy danh sách tin nhắn giữa 2 người
    @GetMapping("/conversation/{partnerId}")
    public Map<String, Object> getConversation2(
    		@PathVariable Long partnerId,
    		@AuthenticationPrincipal CustomUserDetails userDetails) {

    	User sender = new User();
		sender.setId(1l);
		
		
        if (userDetails != null) {
        	sender.setId(userDetails.getId());
        }

        List<ChatMessage> messages = chatRepo.getMessagesBetweenUsers(
        		sender.getId(), partnerId
        );

        Map<String, Object> response = new HashMap<>();
        response.put("currentUserId", sender.getId());
        response.put("messages", messages);

        return response;
    }
    
    
    
    // API: Lấy người đã từng nhắn với user + tin nhắn gần nhất
    @GetMapping("/last-messages")
    public List<Map<String, Object>> getLastMessages(@AuthenticationPrincipal CustomUserDetails userDetails) {
    	
    	Long currentUserId = 1L;
        if (userDetails != null) {
        	currentUserId = userDetails.getId();
        }
    	
        List<ChatMessage> allMessages = chatRepo.findAllMessagesRelatedToUser(currentUserId);
        
        logger.info("currentUserId " + currentUserId + " allMessages " + allMessages.size());

        Map<Long, ChatMessage> latestMap = new LinkedHashMap<>();

        for (ChatMessage msg : allMessages) {
            Long partnerId = msg.getSender().getId().equals(currentUserId) ? msg.getReceiver().getId() : msg.getSender().getId();
            if (!latestMap.containsKey(partnerId)) {
                latestMap.put(partnerId, msg); // Tin nhắn mới nhất với mỗi người
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, ChatMessage> entry : latestMap.entrySet()) {
            ChatMessage msg = entry.getValue();
            Long partnerId = entry.getKey();

            User partner = msg.getSender().getId().equals(currentUserId) ? msg.getReceiver() : msg.getSender();

            Map<String, Object> item = new HashMap<>();
            item.put("partnerId", partnerId);
            item.put("fullName", partner.getFullName());
            item.put("avatarUrl", partner.getAvatarUrl());
            item.put("lastMessage", msg.getContent());
            item.put("read", msg.getSender().getId().equals(currentUserId) || msg.isRead());
            item.put("timestamp", msg.getTimestamp());

            result.add(item);
        }


        return result;
    }
    
    
    @PostMapping("/mark-read")
    public ResponseEntity<?> markMessagesAsRead(
    		@RequestBody ChatMessage request,
    		@AuthenticationPrincipal CustomUserDetails userDetails) {
    	
    	logger.info("request " + request.getSender());
    	
    	Long currentUserId = 1L;

        if (userDetails != null) {
        	currentUserId = userDetails.getId();
        }
        
        
        chatService.markMessagesAsRead(request.getSender().getId(), currentUserId);
        return ResponseEntity.ok().body("Đã đánh dấu là đã đọc");
    }
    
    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadMessageCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
    	Long currentUserId = 1L;
        if (userDetails != null) {
        	currentUserId = userDetails.getId();
        }
        
        
        int count = chatService.countUnreadMessages(currentUserId);
        return ResponseEntity.ok(count);
    }

    
    

}
