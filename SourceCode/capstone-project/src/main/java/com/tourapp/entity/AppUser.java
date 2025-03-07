package com.tourapp.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.validation.annotation.Validated;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$", message = "Mật khẩu phải có ít nhất 8 ký tự, 1 ký tự viết hoa và 1 ký tự đặc biệt (!@#$%^&*)")
    private String password;

    @Pattern(regexp = "0\\d{9}", message = "Số điện thoại phải bắt đầu bằng số 0 và có 10 chữ số")
    private String phone;

    private String facebook;
    private String tiktok;
    private String instagram;
    private String profilePicture;
    private String city;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Thuộc tính dành riêng cho hướng dẫn viên du lịch
    private String bio;
    
    @ElementCollection
    @CollectionTable(name = "tour_guide_languages", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "language")
    private List<String> languages;

    @ElementCollection
    @CollectionTable(name = "tour_guide_cities", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "city")
    private List<String> cities;

    private double rating;
    private String guideLicense;
    private String experience;

    // Thuộc tính dành riêng cho khách du lịch
    private String preferences;
}
