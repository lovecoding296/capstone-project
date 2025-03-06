package com.tourapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status; // "PENDING", "CONFIRMED", "CANCELLED"

    @ManyToOne
    @JoinColumn(name = "tourist_id")
    private Tourist tourist;

    @ManyToOne
    @JoinColumn(name = "tour_id")
    private Tour tour;
}