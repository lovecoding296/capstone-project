package funix.tgcp.guide.service;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonIgnore;

import funix.tgcp.user.City;
import funix.tgcp.user.Language;
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
public class GuideService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "guide_id", nullable = false)
    @JsonIgnore
    private User guide;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType type;  // Enum: CITY_TOUR, NATURE_TOUR, TRANSLATOR, etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupSizeCategory groupSizeCategory;  // Enum: UNDER_5, UNDER_10, OVER_10

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private City city;

    private Double price;
}