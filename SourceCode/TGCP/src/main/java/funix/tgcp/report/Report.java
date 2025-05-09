package funix.tgcp.report;

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
import jakarta.persistence.ManyToOne;
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
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter; // Người gửi báo cáo

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType; // USER or POST

    
    @Column(nullable = false)
    private Long targetId; // ID của người dùng hoặc bài post
    
    @Column(nullable = false, columnDefinition = "NVARCHAR(1000)")
    private String targetName;
    
    @Column(nullable = false, columnDefinition = "NVARCHAR(1000)")
    private String reason;

    private LocalDateTime reportTime = LocalDateTime.now();

    
    private LocalDateTime resolvedTime;
    private boolean resolved = false;
    
    @Column(columnDefinition = "NVARCHAR(1000)")
    private String adminFeedBack;
}
