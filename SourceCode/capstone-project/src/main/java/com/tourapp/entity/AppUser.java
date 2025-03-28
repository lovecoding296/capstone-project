package com.tourapp.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.validation.annotation.Validated;
import java.util.Set;

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

    @Column(columnDefinition = "NVARCHAR(255)")
    private String name;

    @Email(message = "Địa chỉ email không đúng định dạng")
    @Column(nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$", message = "Mật khẩu phải có ít nhất 8 ký tự, 1 ký tự viết hoa và 1 ký tự đặc biệt (!@#$%^&*)")
    @Column(nullable = false)
    private String password;

    @Pattern(regexp = "0\\d{9}", message = "Số điện thoại phải bắt đầu bằng số 0 và có 10 chữ số")
    private String phone;

    private String facebook;
    private String tiktok;
    private String instagram;
    
    @Enumerated(EnumType.STRING) 
    private Gender gender; 
    
    @Column(columnDefinition = "FLOAT DEFAULT 0")
    private double averageRating;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int totalReviews;    
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String city;

    @Enumerated(EnumType.STRING)
    private Role role;
    
    
    private String verificationToken;
    private boolean verified = false; // Chỉ cho phép đăng nhập nếu user đã xác thực email    
    private boolean kycApproved = false; //Chỉ cho phép đăng nhập nếu admin đã duyệt KYC dành cho guide/agency
    private String profilePicture;
    private String cccd;
    private String guideLicensePicture;
    private String agencyLicensePicture;

    // Thuộc tính dành riêng cho hướng dẫn viên du lịch
    @Column(columnDefinition = "NVARCHAR(255)")
    private String bio;
    
    @ElementCollection
    @CollectionTable(name = "guide_languages", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "language", columnDefinition = "NVARCHAR(255)")
    private Set<String> languages;
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String guideLicense;
    
    @Column(columnDefinition = "NVARCHAR(2000)")
    private String experience;

    // Thuộc tính dành riêng cho khách du lịch
    @Column(columnDefinition = "NVARCHAR(255)")
    private String preferences;
    
    
}
