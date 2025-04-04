package funix.tgcp.tour;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import org.springframework.validation.annotation.Validated;

import funix.tgcp.user.User;
import funix.tgcp.tour.image.TourImage;
import funix.tgcp.tour.itinerary.Itinerary;
import funix.tgcp.user.Language;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String title;
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String city;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;
    
    private double price;
    
    private boolean ageRestricted;
    int fromAge;
    int toAge;
    
    
    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private Set<TourImage> images = new HashSet<>();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
	private TourCategory category;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User creator; // Người tổ chức Tour
    
    @ElementCollection(targetClass = Language.class)
    @CollectionTable(name = "tour_languages", joinColumns = @JoinColumn(name = "tour_id"))
    @Enumerated(EnumType.STRING) // Lưu dưới dạng chuỗi
    @Column(name = "language", nullable = false)
    private Set<Language> languages = new HashSet<>();


//    @Min(value = 2, message = "Số lượng người tham gia phải lớn hơn 1")
    @Column(nullable = false)
    private int maxParticipants;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "tour_members",
        joinColumns = @JoinColumn(name = "tour_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants;
    
    
    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dayNo ASC, id ASC")
    private Set<Itinerary> itineraries = new HashSet<>();
    
    @Enumerated(EnumType.STRING)
	private TourStatus status = TourStatus.PENDING;
    
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String rejectedReason;
    
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }  
    
    
}

