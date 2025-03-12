package funix.tca.entity;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.validation.annotation.Validated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @JoinColumn(name = "reviewer_id")
    private AppUser reviewer; // Người đánh giá

    @ManyToOne
    @JoinColumn(name = "reviewed_user_id")
    private AppUser reviewedUser; // Người được đánh giá

}

