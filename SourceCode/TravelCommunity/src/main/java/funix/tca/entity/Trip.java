package funix.tca.entity;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.validation.annotation.Validated;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String title;
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String destination;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    @Column(columnDefinition = "NVARCHAR(1000)")
    private String itinerary; // Lịch trình chi tiết 
    
    @Column(columnDefinition = "NVARCHAR(2000)")
    private String description; //
    
    private double estimatedCost;
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String tripPictureUrl;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser creator; // Người tổ chức chuyến đi

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
        name = "trip_members",
        joinColumns = @JoinColumn(name = "trip_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )    
    private List<AppUser> participants; // Danh sách thành viên tham gia
}

