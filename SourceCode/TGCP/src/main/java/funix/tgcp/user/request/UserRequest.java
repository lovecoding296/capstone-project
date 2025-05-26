package funix.tgcp.user.request;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonIgnore;

import funix.tgcp.guide.service.GroupSizeCategory;
import funix.tgcp.guide.service.GuideService;
import funix.tgcp.guide.service.ServiceType;
import funix.tgcp.user.City;
import funix.tgcp.user.Gender;
import funix.tgcp.user.Language;
import funix.tgcp.user.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table
public class UserRequest {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, columnDefinition = "NVARCHAR(255)")
	private String fullName;
	
	@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "The email is invalid")
	@Column(nullable = false)
	private String email;
	
	@Column(nullable = false)
	@JsonIgnore
	private String password;
	
	@Column(columnDefinition = "NVARCHAR(255)")
	private String cccdUrl;//cccd url
	
	private String verificationToken;
}
