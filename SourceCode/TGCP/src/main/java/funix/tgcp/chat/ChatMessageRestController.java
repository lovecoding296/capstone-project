package funix.tgcp.chat;

import java.util.HashMap;
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

@RestController
@RequestMapping("/api/chat")
public class ChatMessageRestController {
	
    
    @Autowired
    private ChatMessageService chatService;
    
    @GetMapping("/conversation/{partnerId}")
    public Map<String, Object> getMessagesBetweenUsers(
            @PathVariable Long partnerId,
            @AuthenticationPrincipal CustomUserDetails auth) {

        Long currentUserId = auth.getId();
        List<ChatMessage> messages = chatService.getMessagesBetweenUsers(currentUserId, partnerId);

        Map<String, Object> response = new HashMap<>();
        response.put("currentUserId", currentUserId);
        response.put("messages", messages);

        return response;
    }
    
    // API: Lấy người đã từng nhắn với user + tin nhắn gần nhất
    @GetMapping("/last-messages")
    public List<Map<String, Object>> getLastMessages(@AuthenticationPrincipal CustomUserDetails auth) {
        Long currentUserId = auth.getId();
        return chatService.findLatestMessagePerPartner(currentUserId);
    }

    
    
    @PostMapping("/mark-read")
    public ResponseEntity<?> markMessagesAsRead(
    		@RequestBody ChatMessage request,
    		@AuthenticationPrincipal CustomUserDetails auth) {
    	
    	Long currentUserId = auth.getId();              
        chatService.markMessagesAsRead(request.getSender().getId(), currentUserId);
        return ResponseEntity.ok().body("Đã đánh dấu là đã đọc");
    }
    
    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadMessageCount(@AuthenticationPrincipal CustomUserDetails auth) {
    	Long currentUserId = auth.getId();
        
        int count = chatService.countUnreadMessages(currentUserId);
        return ResponseEntity.ok(count);
    }

    
    

}
