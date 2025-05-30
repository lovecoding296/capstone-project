package funix.tgcp.booking.review;

import java.time.LocalDateTime;

import org.springframework.validation.annotation.Validated;

import funix.tgcp.user.User;
import funix.tgcp.booking.Booking;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    @Max(5)
    private int rating; // 1-5 sao
    
    @Column(columnDefinition = "NVARCHAR(1000)", nullable = false)
    private String feedback;
    
    @Column(nullable = false)
    private LocalDateTime reviewDate;
    
    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer; // Người đánh giá

    @ManyToOne
    @JoinColumn(name = "reviewed_user_id", nullable = false)
    private User reviewedUser; // Người được đánh giá
    
    @PrePersist
    protected void onCreate() {
    	reviewDate = LocalDateTime.now();
    } 

}

