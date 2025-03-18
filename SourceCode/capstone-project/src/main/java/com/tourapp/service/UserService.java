package com.tourapp.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tourapp.entity.AppUser;
import com.tourapp.entity.Role;
import com.tourapp.exception.EmailAlreadyExistsException;
import com.tourapp.exception.EmailVerificationException;
import com.tourapp.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private EmailService emailService; // Service gửi email
	
    public void verifyEmail(String token) throws EmailVerificationException {
        Optional<AppUser> optionalUser = userRepository.findByVerificationToken(token);

        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();
            user.setVerified(true);
            user.setVerificationToken(null); // Xóa token sau khi xác thực
            userRepository.save(user);
        } else {
            throw new EmailVerificationException("Invalid verification token or the token has expired.");
        }
    }
	

	public void save(AppUser user) {
		userRepository.save(user);
	}

	public AppUser registerUser(AppUser user, Role role) throws EmailAlreadyExistsException {
		if (userRepository.existsByEmail(user.getEmail())) {
			logger.warn("Attempt to register with existing email: {}", user.getEmail());
			throw new EmailAlreadyExistsException("Email already exists");
		}
		
		// Tạo token ngẫu nhiên
        String token = UUID.randomUUID().toString();
        logger.info("Tạo token ngẫu nhiên " + token);
        
        user.setVerificationToken(token);
        user.setVerified(false);
		
		emailService.sendVerificationEmail(user.getEmail(), token);

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(role);

		AppUser savedUser = userRepository.save(user);
		logger.info("New user registered: {} with role {}", savedUser.getEmail(), savedUser.getRole());

		return savedUser;
	}

	public AppUser findByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new AccessDeniedException("User not found with email: " + email));
	}

	public AppUser getUserById(Long id) {
		return userRepository.findUserById(id)
				.orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));
	}

	public AppUser updateCurrentUser(AppUser user, MultipartFile file) throws IOException {

		AppUser currentUser = getCurrentAuthenticatedUser();

		if (user == null) {
			throw new IllegalArgumentException("Updated user information cannot be null.");
		}

		if (user.getName() != null && !user.getName().isEmpty()) {
			currentUser.setName(user.getName());
		}
		if (user.getPhone() != null && !user.getPhone().isEmpty()) {
			currentUser.setPhone(user.getPhone());
		}
		if (user.getFacebook() != null && !user.getFacebook().isEmpty()) {
			currentUser.setFacebook(user.getFacebook());
		}
		if (user.getTiktok() != null && !user.getTiktok().isEmpty()) {
			currentUser.setTiktok(user.getTiktok());
		}
		if (user.getInstagram() != null && !user.getInstagram().isEmpty()) {
			currentUser.setInstagram(user.getInstagram());
		}
		if (user.getCity() != null && !user.getCity().isEmpty()) {
			currentUser.setCity(user.getCity());
		}
		if (user.getBio() != null && !user.getBio().isEmpty()) {
			currentUser.setBio(user.getBio());
		}
		if (user.getLanguages() != null && !user.getLanguages().isEmpty()) {
			currentUser.setLanguages(user.getLanguages());
		}
		if (user.getGuideLicense() != null && !user.getGuideLicense().isEmpty()) {
			currentUser.setGuideLicense(user.getGuideLicense());
		}
		if (user.getExperience() != null && !user.getExperience().isEmpty()) {
			currentUser.setExperience(user.getExperience());
		}
		if (user.getPreferences() != null && !user.getPreferences().isEmpty()) {
			currentUser.setPreferences(user.getPreferences());
		}

		// Xử lý ảnh đại diện
		if (file != null && !file.isEmpty()) {
			uploadProfilePicture(currentUser, file);
		} else {
			logger.info("No file chosen.");
		}

		return userRepository.save(currentUser);
	}

	public AppUser getCurrentAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()
				|| "anonymousUser".equals(authentication.getPrincipal())) {
			throw new AccessDeniedException("Access Denied! Please log in.");
		}

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		return userRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new AccessDeniedException("User not found."));
	}

	private void uploadProfilePicture(AppUser user, MultipartFile file) throws IOException {
		// Kiểm tra định dạng file hợp lệ
		Set<String> allowedMimeTypes = Set.of("image/jpeg", "image/png", "image/webp");
		String contentType = file.getContentType();
		if (contentType == null || !allowedMimeTypes.contains(contentType)) {
			throw new IllegalArgumentException("Invalid file type. Only JPEG, PNG, and WEBP are allowed.");
		}

		String emailName = user.getEmail().replaceAll("@.+$", "");
		String fileName = emailName + "_" + file.getOriginalFilename();
		String uploadDir = "uploads/";

		File uploadPath = new File(uploadDir);
		if (!uploadPath.exists()) {
			uploadPath.mkdirs();
		}

		File destination = new File(uploadDir + fileName);
		Files.copy(file.getInputStream(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

		user.setProfilePicture("/uploads/" + fileName);
	}

	@Transactional
	public void updateGuideRating(Long id, int newRating) {

		AppUser user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

		// Cập nhật rating trung bình
		double newAverage = (user.getAverageRating() * user.getTotalReviews() + newRating)
				/ (user.getTotalReviews() + 1);
		user.setTotalReviews(user.getTotalReviews() + 1);
		user.setAverageRating(newAverage);

		userRepository.save(user);

	}

	public List<AppUser> findTop4ByOrderByAverageRatingDesc() {		
		return userRepository.findTop4ByOrderByAverageRatingDesc();
	}

}
