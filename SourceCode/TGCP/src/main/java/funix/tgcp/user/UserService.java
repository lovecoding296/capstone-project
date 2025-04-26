package funix.tgcp.user;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.booking.BookingController;
import funix.tgcp.exception.EmailAlreadyExistsException;
import funix.tgcp.exception.EmailVerificationException;
import funix.tgcp.util.EmailHelper;
import funix.tgcp.util.FileHelper;
import funix.tgcp.util.LogHelper;

@Service
public class UserService {
	
	private static final LogHelper logger = new LogHelper(UserService.class);


    @Autowired
    private UserRepository userRepo;
    
    @Autowired
	private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailHelper emailHelper;
    
    @Autowired
	private FileHelper fileHelper;

    public List<User> getUnapprovedUsers() {
        return userRepo.findByKycApprovedFalse();
    }

    // Tìm User theo ID
    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }

    // Tìm User theo email
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    // Lấy danh sách tất cả User
    public List<User> findAll() {
        return userRepo.findAll();
    }

    // Cập nhật thông tin User
    public User update(User userToUpdate) {
        return userRepo.save(userToUpdate);
    }

    // Xóa User theo ID
    public void deleteById(Long id) {
    	userRepo.deleteById(id);
    }
    
    public User registerUser(MultipartFile cccdFile, User user) throws EmailAlreadyExistsException, IOException {
		if (userRepo.existsByEmail(user.getEmail())) {
			System.out.println("Attempt to register with existing email: " + user.getEmail());
			throw new EmailAlreadyExistsException("Email already exists");
		}
		
		String cccd = fileHelper.uploadFile(cccdFile);			
		if(cccd != null) {
    		user.setCccd(cccd);
    	}
		
		// Tạo token ngẫu nhiên
        String token = UUID.randomUUID().toString();
        System.out.println("Tạo token ngẫu nhiên " + token);
        
        user.setVerificationToken(token);
        user.setVerified(false);        
        
        user.setAvatarUrl("/uploads/default-avatar.jpg");        
        
		emailHelper.sendVerificationEmail(user.getEmail(), token);

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		User savedUser = userRepo.save(user);
		return savedUser;
	}

	public void verifyEmail(String token) throws EmailVerificationException{
		Optional<User> optionalUser = userRepo.findByVerificationToken(token);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setVerified(true);
            user.setVerificationToken(null); // Xóa token sau khi xác thực
            userRepo.save(user);
        } else {
            throw new EmailVerificationException("Invalid verification token or the token has expired.");
        }
	}
	
	public boolean approveuser(Long id) {
		Optional<User> currentUserOption = findById(id);

		if (currentUserOption.isPresent()) {
			User currentUser = currentUserOption.get();
			currentUser.setKycApproved(true);
			userRepo.save(currentUser);
			return true;
		}
		return false;		
	}
	
	public boolean rejectuser(Long id) {
		Optional<User> currentUserOption = findById(id);

		if (currentUserOption.isPresent()) {
			User currentUser = currentUserOption.get();
			currentUser.setKycApproved(false);
			userRepo.save(currentUser);
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
		
		if (user.getBankName() != null && !user.getBankName().isEmpty()) {
			currentUser.setBankName(user.getBankName());
		}
		
		if (user.getAccountNumber() != null && !user.getAccountNumber().isEmpty()) {
			currentUser.setAccountNumber(user.getAccountNumber());
		}
		
		if (user.getAccountHolder() != null && !user.getAccountHolder().isEmpty()) {
			currentUser.setAccountHolder(user.getAccountHolder());
		}
		
		currentUser.setPricePerDay(user.getPricePerDay());

		userRepo.save(currentUser);
	}

	public List<User> getTopByRoleOrderByAverageRatingDesc(Role role) {
		return userRepo.findTop6ByRoleAndKycApprovedTrueAndVerifiedTrueAndIsActiveTrueOrderByAverageRatingDesc(role);
	}

	public List<User> searchGuides(City city, Integer maxPrice, Gender gender, Language language, int page, int size) {
        List<User> allGuides = userRepo.findByRole(Role.ROLE_GUIDE);

        return allGuides.stream()
                .filter(user -> city == null || user.getCity() == city)
                .filter(user -> maxPrice == null || user.getPricePerDay() <= maxPrice)
                .filter(user -> gender == null || user.getGender() == gender)
                .filter(user -> language == null || user.getLanguages().contains(language))
                .collect(Collectors.toList());
    }

	public Page<User> searchGuides(City city, Integer maxPrice, Gender gender, Language language, Boolean isLocalGuide, Boolean isInternationalGuide, Pageable pageable) {
		return userRepo.findGuideByFilter(city, maxPrice, gender, language,isLocalGuide, isInternationalGuide, pageable);
	}
	
	
	public void updateRating(User user, int newRating) {
		int currentCount = user.getReviewCount();
		double currentAverage = user.getAverageRating();
		
		int newCount = currentCount + 1;
		double newAverage = (currentAverage * currentCount + newRating) / newCount;

		user.setReviewCount(newCount);
		user.setAverageRating(newAverage);
		
		userRepo.save(user);
	}

	public void setUserEnabled(Long id, boolean b) {
		userRepo.findById(id).ifPresent(user -> {
			user.setEnabled(b);
			userRepo.save(user);
		});
		
	}

	public void approveKyc(Long id) {
		userRepo.findById(id).ifPresent(user -> {
			user.setKycApproved(true);
			userRepo.save(user);
		});		
	}

	public Page<User> findUserByFilter(String email, String fullName, Role role, Boolean kycApproved, Boolean enabled,
			Boolean verified, Pageable pageable) {
		return userRepo.findUserByFilter(email, fullName, role, kycApproved, enabled, verified, pageable);
	}

	public void forgotPassword(String email) {
		logger.info("email " + email);
		Optional<User> userOp = findByEmail(email);
		
		if(userOp.isPresent()) {
			
			String token = UUID.randomUUID().toString();
	        System.out.println("Tạo token ngẫu nhiên " + token);	      
	        
	        logger.info("create token " + token);
	        
	        
	        userOp.get().setVerificationToken(token);
	        userRepo.save(userOp.get());
	        emailHelper.sendForgotPasswordEmail(email, token);
		}
	}

	public void handleResetPassword(String token, String newPassword) throws EmailVerificationException {
		logger.info("token " + token + " newPassword " + newPassword);
		
		
		Optional<User> optionalUser = userRepo.findByVerificationToken(token);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setVerificationToken(null);
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(user);
        } else {
            throw new EmailVerificationException("Invalid verification token or the token has expired.");
        }
		
	}
}
