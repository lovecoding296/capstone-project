package funix.tgcp.user;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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

	@Email(message = "Địa chỉ email không đúng định dạng")
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

	// @Pattern(regexp = "0\\d{9}", message = "Số điện thoại phải bắt đầu bằng số 0
	// và có 10 chữ số")
	private String phone;

	private String facebook;
	private String tiktok;
	private String instagram;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Enumerated(EnumType.STRING)
	private Role role = Role.ROLE_USER;

	@ElementCollection(targetClass = Language.class)
	@CollectionTable(name = "user_languages", joinColumns = @JoinColumn(name = "user_id"))
	@Enumerated(EnumType.STRING) // Lưu Enum dưới dạng String
	@Column(name = "language", nullable = false)
	private Set<Language> languages;


	@Column(columnDefinition = "NVARCHAR(255)")
	private String city;

	@Column(columnDefinition = "NVARCHAR(255)")
	private String homeAddress;

	private double averageRating;

	private String verificationToken;
	private boolean verified = false; // verify email
	private boolean kycApproved = false; //verify cccd
	
	private String cccd;//cccd url
	
	private boolean isActive = true;

	public boolean isAdmin() {
		return this.role == Role.ROLE_ADMIN;
	}
	
    private String bankName; // Tên ngân hàng

    private String accountNumber; // Số tài khoản

    private String accountHolder; // Chủ tài khoản


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