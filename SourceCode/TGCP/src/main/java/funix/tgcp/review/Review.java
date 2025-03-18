package funix.tgcp.review;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.validation.annotation.Validated;

import funix.tgcp.appuser.AppUser;
import funix.tgcp.trip.Trip;
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


/**
 * Sau mỗi chuyến đi, thành viên có thể đánh giá lẫn nhau.
 * 
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating; // 1-5 sao
    
    @Column(columnDefinition = "NVARCHAR(1000)")
    private String feedback;
    
    private LocalDateTime reviewDate;
    
    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip; // Đánh giá thuộc về chuyến đi nào

    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    private AppUser reviewer; // Người đánh giá

    @ManyToOne
    @JoinColumn(name = "reviewed_user_id", nullable = false)
    private AppUser reviewedUser; // Người được đánh giá
    
    @PrePersist
    protected void onCreate() {
    	reviewDate = LocalDateTime.now();
    } 

}

