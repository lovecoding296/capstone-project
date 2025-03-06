package com.tourapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import org.springframework.validation.annotation.Validated;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class TourGuide {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
	private String password;
	
    @Pattern(regexp ="0\\d{9}", message = "Số điện thoại phải bắt đầu bằng số 0 và có 10 chữ số")
    private String phone;
    
    private String facebook;
    private String tiktok;
    private String instagram;
    
    private String avatar;
}

