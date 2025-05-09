package funix.tgcp.user;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonIgnore;

import funix.tgcp.guide.service.GroupSizeCategory;
import funix.tgcp.guide.service.GuideService;
import funix.tgcp.guide.service.ServiceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Validated
@Table(name = "app_user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, columnDefinition = "NVARCHAR(255)")
	private String fullName;

	@Email(message = "Email address is not in correct format")
	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	@JsonIgnore
	private String password;

	@Column(columnDefinition = "NVARCHAR(255)")
	private String avatarUrl;

	@Column(columnDefinition = "NVARCHAR(1000)")
	private String bio;

	@Column(columnDefinition = "NVARCHAR(255)")
	private String interests;

	@Column
    private LocalDate dateOfBirth;

	@Pattern(regexp = "0\\d{9}", message = "Phone number must start with 0 and have 10 digits")
	private String phone;

	private String facebook;
	private String tiktok;
	private String instagram;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Enumerated(EnumType.STRING)
	private Role role = Role.ROLE_USER;

	private double averageRating;

	@JsonIgnore
	private String verificationToken;
		
	private boolean kycApproved = false; // verify cccd
	
	private String kycRejectionReason;
	
	private boolean enabled = true; // report
	
	@Column(columnDefinition = "NVARCHAR(255)")
	private String cccdUrl;//cccd url
	
	public boolean isAdmin() {
		return this.role == Role.ROLE_ADMIN;
	}
	
	public boolean isGuide() {
		return this.role == Role.ROLE_GUIDE;
	}	
	
	@Column(columnDefinition = "NVARCHAR(255)")
	private String certificateUrl;
	
	@Column(columnDefinition = "NVARCHAR(255)")
    private String certificateNumber;
    
    @Column(columnDefinition = "NVARCHAR(2000)")
    private String experience;
    
    private String bankName; // Tên ngân hàng

    private String accountNumber; // Số tài khoản

    private String accountHolder; // Chủ tài khoản
    
    private int reviewCount = 0; // số người đánh giá    
    
    private boolean isLocalGuide;
    private boolean isInternationalGuide;
    
    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("type ASC, city ASC, language ASC, groupSizeCategory ASC")
    private Set<GuideService> guideServices = new HashSet<>();  
    
    @Transient
    private Set<City> cities;

    @Transient
    private Set<Language> languages;

    @Transient
    private Set<ServiceType> serviceTypes;
    
    @Transient
    private Set<GroupSizeCategory> groupSizes;
    
	@Override
	public boolean equals(Object obj) {
			
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		User user = (User) obj;
		return this.getId().equals(user.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}