package com.tourapp.entity;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String title;
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String description;
    
    private double price;
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String destination;
    
    private String thumbnail;
    private String thumbnail2;
    private String thumbnail3;
    private String thumbnail4;
    
    private LocalDate startDate;
    private LocalDate endDate;
    
    @Column(columnDefinition = "FLOAT DEFAULT 0")
    private double averageRating;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int totalReviews;    
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String type;

    @ManyToOne()
    @JoinColumn(name = "guide_id")
    private AppUser guide;
    
    @ManyToOne()
    @JoinColumn(name = "agency_id")
    private AppUser agency;
    
    
    @ElementCollection
    @CollectionTable(name = "tour_languages", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "language", columnDefinition = "NVARCHAR(255)")
    private Set<String> languages;
}