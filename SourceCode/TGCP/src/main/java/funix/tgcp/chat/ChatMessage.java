package funix.tgcp.chat;

import java.time.LocalDateTime;

import org.springframework.validation.annotation.Validated;

import funix.tgcp.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;     
    
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;   

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    private LocalDateTime timestamp;
    
    private boolean isRead = false;
    
    @PrePersist
    protected void onCreate() {
    	timestamp = LocalDateTime.now();
    }
}