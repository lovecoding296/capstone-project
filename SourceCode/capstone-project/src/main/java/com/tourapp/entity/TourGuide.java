package com.tourapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.util.List;
import org.springframework.validation.annotation.Validated;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class TourGuide {
	
	@Id
	private Long id;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "id")
	private Account account;
   
    private String bio;

    @ElementCollection
    @CollectionTable(name = "tour_guide_languages", joinColumns = @JoinColumn(name = "tour_guide_id"))
    @Column(name = "language")
    private List<String> languages;

    @ElementCollection
    @CollectionTable(name = "tour_guide_cities", joinColumns = @JoinColumn(name = "tour_guide_id"))
    @Column(name = "city")
    private List<String> cities;

    private double rating;
    private String guideLicense;
    private String experience;
}

