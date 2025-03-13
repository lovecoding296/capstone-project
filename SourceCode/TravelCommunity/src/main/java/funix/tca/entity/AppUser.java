package funix.tca.entity;

import java.util.Set;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonIgnore;

import funix.tca.enums.Gender;
import funix.tca.enums.Language;
import funix.tca.enums.Role;
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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
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
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String fullName;
    
    @Email(message = "Địa chỉ email không đúng định dạng")
    @Column(nullable = false, unique = true)
    private String email;
    
    //@Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$", message = "Mật khẩu phải có ít nhất 8 ký tự, 1 ký tự viết hoa và 1 ký tự đặc biệt (!@#$%^&*)")
    @Column(nullable = false)
    private String password;
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String avatarUrl;
    
    @Column(columnDefinition = "NVARCHAR(1000)")
    private String bio; // Giới thiệu bản thân
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String interests; // Sở thích du lịch
    
    private int age;
    
    @Pattern(regexp = "0\\d{9}", message = "Số điện thoại phải bắt đầu bằng số 0 và có 10 chữ số")
    private String phone;
    
    @Pattern(regexp = "0\\d{9}", message = "Số điện thoại phải bắt đầu bằng số 0 và có 10 chữ số")
    private String familyPhone;
    
    private String facebook;
    private String tiktok;
    private String instagram;

    @Enumerated(EnumType.STRING) 
    private Gender gender; 
    
    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER;    
    
    @ElementCollection(targetClass = Language.class)
    @CollectionTable(name = "user_languages", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    @JsonIgnore
    private Set<Language> languages;
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String city;
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String homeAddress;
    
    private double averageRating; // Điểm uy tín từ đánh giá
    
    //auth
    private String verificationToken;
    private boolean verified = false; // Chỉ cho phép đăng nhập nếu user đã xác thực email  
    private boolean kycApproved = false;  //Chỉ cho phép tạo trip nếu admin đã duyệt ảnh và tên
    private String cccd; //Căn cước công dân
    
	public boolean isAdmin() {
		return this.role == Role.ROLE_ADMIN;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }
		if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
		AppUser user = (AppUser) obj;
		return this.getId() == user.getId();
	}
}
