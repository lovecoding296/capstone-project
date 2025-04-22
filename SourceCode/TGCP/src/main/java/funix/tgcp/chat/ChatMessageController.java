package funix.tgcp.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import funix.tgcp.booking.BookingController;
import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.user.User;
import funix.tgcp.user.UserRepository;
import funix.tgcp.util.LogHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatMessageController {
	
	private static final LogHelper logger = new LogHelper(ChatMessageController.class);


    @Autowired
    private ChatMessageRepository chatRepo;
    
    @Autowired
    private ChatMessageService chatService;
    
    @Autowired
    private UserRepository userRepository;

    // Gửi tin nhắn
    @PostMapping("/send")
    public ChatMessage sendMessage(@RequestBody ChatMessage message) {
        message.setTimestamp(java.time.LocalDateTime.now());
        
        User sender = new User();
		
    	CustomUserDetails userDetails = CustomUserDetails.getCurrentUserDetails();
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
    public Map<String, Object> getConversation2(@PathVariable Long partnerId) {

    	User sender = new User();
		sender.setId(1l);
		
		
        CustomUserDetails userDetails = CustomUserDetails.getCurrentUserDetails();
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
    public List<Map<String, Object>> getLastMessages() {
    	
    	Long currentUserId = 1L;
        CustomUserDetails userDetails = CustomUserDetails.getCurrentUserDetails();
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
            item.put("isRead", msg.isRead());
            item.put("timestamp", msg.getTimestamp());

            result.add(item);
        }


        return result;
    }
    
    
    @PostMapping("/mark-read")
    public ResponseEntity<?> markMessagesAsRead(@RequestBody ChatMessage request) {
    	Long currentUserId = 1L;
        CustomUserDetails userDetails = CustomUserDetails.getCurrentUserDetails();
        if (userDetails != null) {
        	currentUserId = userDetails.getId();
        }
        
        
        chatService.markMessagesAsRead(request.getSender().getId(), currentUserId);
        return ResponseEntity.ok().body("Đã đánh dấu là đã đọc");
    }
    
    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadMessageCount() {
    	Long currentUserId = 1L;
        CustomUserDetails userDetails = CustomUserDetails.getCurrentUserDetails();
        if (userDetails != null) {
        	currentUserId = userDetails.getId();
        }
        
        
        int count = chatService.countUnreadMessages(currentUserId);
        return ResponseEntity.ok(count);
    }

    
    

}
