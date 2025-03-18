package funix.tgcp.trip;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.validation.annotation.Validated;

import funix.tgcp.user.User;
import funix.tgcp.trip.image.TripImage;
import funix.tgcp.trip.itinerary.Itinerary;
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
    private String city;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;
    
    private double price;
    
    private boolean ageRestricted;
    int fromAge;
    int toAge;
    
    
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<TripImage> images = new HashSet<>(); // Lưu danh sách đường dẫn ảnh
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
	private TripCategory category;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User creator; // Người tổ chức chuyến đi
    
    @ElementCollection(targetClass = Language.class)
    @CollectionTable(name = "trip_languages", joinColumns = @JoinColumn(name = "trip_id"))
    @Enumerated(EnumType.STRING) // Lưu dưới dạng chuỗi
    @Column(name = "language", nullable = false)
    private Set<Language> languages;


    @Min(value = 2, message = "Số lượng người tham gia phải lớn hơn 1")
    @Column(nullable = false)
    private int maxParticipants;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "trip_members",
        joinColumns = @JoinColumn(name = "trip_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants;
    
    
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Itinerary> itineraries = new HashSet<>();    
    
    @Enumerated(EnumType.STRING)
	private TripStatus status;
    
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }  
}

