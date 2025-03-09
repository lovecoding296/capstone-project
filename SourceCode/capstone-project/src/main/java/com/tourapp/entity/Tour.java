package com.tourapp.entity;

import java.time.LocalDate;

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
    
    private LocalDate startDate;
    private LocalDate endDate;
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String type;

    @ManyToOne
    @JoinColumn(name = "guide_id")
    private AppUser guide;
}