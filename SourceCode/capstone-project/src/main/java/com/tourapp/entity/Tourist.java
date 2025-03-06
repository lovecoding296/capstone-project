package com.tourapp.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class Tourist {
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

	private String avatar;
}
