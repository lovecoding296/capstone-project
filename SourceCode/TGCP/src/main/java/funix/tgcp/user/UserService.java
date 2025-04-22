package funix.tgcp.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import funix.tgcp.exception.EmailAlreadyExistsException;
import funix.tgcp.exception.EmailVerificationException;
import funix.tgcp.util.EmailHelper;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
	private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailHelper emailHelper;
    

    // Lưu một User mới
    public User save(User user) {
    	if (user.getAvatarUrl() == null || user.getAvatarUrl().isBlank()) {
    	    user.setAvatarUrl("/uploads/default-avatar.jpg");
    	}
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public List<User> getUnapprovedUsers() {
        return userRepository.findByKycApprovedFalse();
    }

    // Tìm User theo ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Tìm User theo email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Lấy danh sách tất cả User
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // Cập nhật thông tin User
    public User update(User userToUpdate) {
        return userRepository.save(userToUpdate);
    }

    // Xóa User theo ID
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    
    public User registerUser(User user) throws EmailAlreadyExistsException {
		if (userRepository.existsByEmail(user.getEmail())) {
			System.out.println("Attempt to register with existing email: " + user.getEmail());
			throw new EmailAlreadyExistsException("Email already exists");
		}
		
		// Tạo token ngẫu nhiên
        String token = UUID.randomUUID().toString();
        System.out.println("Tạo token ngẫu nhiên " + token);
        
        user.setVerificationToken(token);
        user.setVerified(false);
		
		emailHelper.sendVerificationEmail(user.getEmail(), token);

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		User savedUser = userRepository.save(user);
		return savedUser;
	}

	public void verifyEmail(String token) throws EmailVerificationException{
		Optional<User> optionalUser = userRepository.findByVerificationToken(token);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setVerified(true);
            user.setVerificationToken(null); // Xóa token sau khi xác thực
            userRepository.save(user);
        } else {
            throw new EmailVerificationException("Invalid verification token or the token has expired.");
        }
	}
	
	public boolean approveuser(Long id) {
		Optional<User> currentUserOption = findById(id);

		if (currentUserOption.isPresent()) {
			User currentUser = currentUserOption.get();
			currentUser.setKycApproved(true);
			userRepository.save(currentUser);
			return true;
		}
		return false;		
	}
	
	public boolean rejectuser(Long id) {
		Optional<User> currentUserOption = findById(id);

		if (currentUserOption.isPresent()) {
			User currentUser = currentUserOption.get();
			currentUser.setKycApproved(false);
			userRepository.save(currentUser);
			return true;
		}
		return false;		
	}
	
	public void updateCurrentUser(User user) {

		Optional<User> currentUserOption = findById(user.getId());

		if (currentUserOption.isEmpty()) {
			throw new IllegalArgumentException("Updated user information cannot be null.");
		}
		
		User currentUser = currentUserOption.get();

		if (user.getFullName() != null && !user.getFullName().isEmpty()) {
			currentUser.setFullName(user.getFullName());
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
		if (user.getCity() != null) {
			currentUser.setCity(user.getCity());
		}
		if (user.getBio() != null && !user.getBio().isEmpty()) {
			currentUser.setBio(user.getBio());
		}
		if (user.getLanguages() != null && !user.getLanguages().isEmpty()) {
			currentUser.setLanguages(user.getLanguages());
		}
		if (user.getBio() != null && !user.getBio().isEmpty()) {
			currentUser.setBio(user.getBio());
		}
		if (user.getInterests() != null && !user.getInterests().isEmpty()) {
			currentUser.setInterests(user.getInterests());
		}

		if (user.getDateOfBirth() != null) {
			currentUser.setDateOfBirth(user.getDateOfBirth());
		}
		
		if (user.getGender() != null) {
			currentUser.setGender(user.getGender());
		}
		
		if (user.getPassword() != null && !user.getPassword().isEmpty()) {
			currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		
		if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
			currentUser.setAvatarUrl(user.getAvatarUrl());
		}
		
		if (user.getCccd() != null && !user.getCccd().isEmpty()) {
			currentUser.setCccd(user.getCccd());
		}
		

		userRepository.save(currentUser);
	}

	public List<User> getTopByRoleOrderByAverageRatingDesc(Role role) {
		return userRepository.findTop6ByRoleAndKycApprovedTrueAndVerifiedTrueAndIsActiveTrueOrderByAverageRatingDesc(role);
	}

	public List<User> searchGuides(City city, Integer maxPrice, Gender gender, Language language, int page, int size) {
        List<User> allGuides = userRepository.findByRole(Role.ROLE_GUIDE); // giả sử bạn có method này

        return allGuides.stream()
                .filter(user -> city == null || user.getCity() == city)
                .filter(user -> maxPrice == null || user.getPricePerDay() <= maxPrice)
                .filter(user -> gender == null || user.getGender() == gender)
                .filter(user -> language == null || user.getLanguages().contains(language))
                .collect(Collectors.toList());
    }

	public Page<User> searchGuides(City city, Integer maxPrice, Gender gender, Language language, Pageable pageable) {
		return userRepository.findGuideByFilter(city, maxPrice, gender, language, pageable);
	}
}
