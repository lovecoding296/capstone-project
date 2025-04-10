package funix.tgcp.report;

import java.time.LocalDateTime;

import org.springframework.validation.annotation.Validated;

import funix.tgcp.user.User;
import funix.tgcp.post.Post;
import funix.tgcp.tour.Tour;
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

/**
 * 
 * Hỗ trợ báo cáo hành vi xấu hoặc nội dung không phù hợp
 * 
 */
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

    private String reason;
    private LocalDateTime reportDate;
    private boolean resolved;

    @ManyToOne
    @JoinColumn(name = "reported_by", nullable = false)
    private User reportedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType; // Loại báo cáo (USER, POST, TRIP)
    
    @ManyToOne
    @JoinColumn(name = "reported_user")
    private User reportedUser; // Nếu báo cáo một người dùng

    @ManyToOne
    @JoinColumn(name = "reported_post")
    private Post reportedPost; // Nếu báo cáo bài viết

    @ManyToOne
    @JoinColumn(name = "reported_tour")
    private Tour reportedTour; // Nếu báo cáo một Tour

}
