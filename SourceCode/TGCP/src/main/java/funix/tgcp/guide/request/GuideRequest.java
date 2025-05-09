package funix.tgcp.guide.request;

import java.time.LocalDateTime;

import org.springframework.validation.annotation.Validated;

import funix.tgcp.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
public class GuideRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User gửi yêu cầu
    
    @Column(columnDefinition = "NVARCHAR(255)", nullable = false)
	private String cccdUrl;

    @Column(columnDefinition = "NVARCHAR(255)", nullable = false)
	private String certificateUrl;
	
	@Column(columnDefinition = "NVARCHAR(255)", nullable = false)
    private String certificateNumber;
    
    @Column(columnDefinition = "NVARCHAR(2000)")
    private String experience;
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String reason;
    
    private boolean isLocalGuide;
    private boolean isInternationalGuide;
    
    @Enumerated(EnumType.STRING)
    private GuideRequestStatus status = GuideRequestStatus.PENDING; // Trạng thái yêu cầu

    private LocalDateTime createdAt = LocalDateTime.now(); // Ngày gửi yêu cầu
}

