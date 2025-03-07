package com.tourapp.entity;

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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private double price;
    private String location;
    
    private String thumbnail_0;
    private String thumbnail_1;
    private String thumbnail_2;
    private String thumbnail_3;
    private String thumbnail_4;
    private String thumbnail_5;

    @ManyToOne
    @JoinColumn(name = "guide_id")
    private AppUser guide;
}