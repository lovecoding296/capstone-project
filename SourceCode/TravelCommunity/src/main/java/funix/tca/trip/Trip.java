package funix.tca.trip;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.validation.annotation.Validated;

import funix.tca.appuser.AppUser;
import funix.tca.appuser.Gender;
import funix.tca.appuser.Language;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String itinerary; // Lịch trình chi tiết 
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description; //
    
    private double estimatedCost;
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String tripPictureUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
	private TripCategory category;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser creator; // Người tổ chức chuyến đi
    
    @ElementCollection(targetClass = Language.class)
    @CollectionTable(name = "trip_languages", joinColumns = @JoinColumn(name = "trip_id"))
    @Enumerated(EnumType.STRING) // Lưu dưới dạng chuỗi
    @Column(name = "language", nullable = false)
    private Set<Language> languages;
    
    @Enumerated(EnumType.STRING)
	private Gender gender;


    @Min(value = 2, message = "Số lượng người tham gia phải lớn hơn 1")
    @Column(nullable = false)
    private int maxParticipants;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "trip_members",
        joinColumns = @JoinColumn(name = "trip_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<AppUser> participants;
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }  
}

