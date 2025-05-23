package funix.tgcp.user;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import funix.tgcp.exception.EmailVerificationException;
import funix.tgcp.guide.service.GroupSizeCategory;
import funix.tgcp.guide.service.GuideService;
import funix.tgcp.guide.service.ServiceType;
import funix.tgcp.util.EmailHelper;
import funix.tgcp.util.LogHelper;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
	
	private static final LogHelper logger = new LogHelper(UserService.class);


    @Autowired
    private UserRepository userRepo;
    
    @Autowired
	private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailHelper emailHelper;

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
	
	public void updateCurrentUser(User user) {
		logger.info("");
	    User currentUser = findById(user.getId())
	        .orElseThrow(() -> new IllegalArgumentException("Updated user information cannot be null."));

	    // Helper method for setting non-null, non-empty fields
	    updateFieldIfNotEmpty(user.getFullName(), currentUser::setFullName);
	    updateFieldIfNotEmpty(user.getPhone(), currentUser::setPhone);
	    updateFieldIfNotEmpty(user.getFacebook(), currentUser::setFacebook);
	    updateFieldIfNotEmpty(user.getTiktok(), currentUser::setTiktok);
	    updateFieldIfNotEmpty(user.getInstagram(), currentUser::setInstagram);
	    updateFieldIfNotEmpty(user.getBio(), currentUser::setBio);
	    updateFieldIfNotEmpty(user.getInterests(), currentUser::setInterests);
	    updateFieldIfNotNull(user.getDateOfBirth(), currentUser::setDateOfBirth);
	    updateFieldIfNotNull(user.getGender(), currentUser::setGender);
	    updateFieldIfNotEmpty(user.getAvatarUrl(), currentUser::setAvatarUrl);
	    updateFieldIfNotEmpty(user.getCccdUrl(), currentUser::setCccdUrl);
	    updateFieldIfNotEmpty(user.getBankName(), currentUser::setBankName);
	    updateFieldIfNotEmpty(user.getAccountNumber(), currentUser::setAccountNumber);
	    updateFieldIfNotEmpty(user.getAccountHolder(), currentUser::setAccountHolder);

	    logger.info("user.getAvatarUrl() " + user.getAvatarUrl());
	    
	    // Update password only if provided
	    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
	        currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
	    }

	    userRepo.save(currentUser);
	}

	// Helper method to update fields if they are not null or empty
	private void updateFieldIfNotEmpty(String newValue, Consumer<String> setter) {
	    if (newValue != null && !newValue.isEmpty()) {
	        setter.accept(newValue);
	    }
	}

	// Helper method to update fields if they are not null
	private <T> void updateFieldIfNotNull(T newValue, Consumer<T> setter) {
	    if (newValue != null) {
	        setter.accept(newValue);
	    }
	}


	public List<User> findTop6UsersWithGuideServicesAndRoleGuide() {
		Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Order.desc("averageRating")));
		return userRepo.findTopUsersWithGuideServicesAndRoleGuide(pageable);
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
	
	public void setUserEnabled(Long id, boolean enabled) {
	    Optional<User> userOpt = userRepo.findById(id);
	    
	    if (userOpt.isPresent()) {
	        User user = userOpt.get();
	        user.setEnabled(enabled);
	        userRepo.save(user);
	    } else {
	        throw new EntityNotFoundException("User not found with id: " + id);
	    }
	}

	public Page<User> findUserByFilter(String email, String fullName, Role role, Boolean enabled,
			Pageable pageable) {
		return userRepo.findUserByFilter(email, fullName, role, enabled, pageable);
	}

	public void forgotPassword(String email) {
		logger.info("email " + email);
		
		if(email == null ) {
			throw new IllegalArgumentException();
		}
		
		Optional<User> userOp = findByEmail(email);
		
		if(userOp.isPresent()) {
			
			String token = UUID.randomUUID().toString();
	        System.out.println("Create random token: " + token);	      
	        
	        logger.info("create token " + token);
	        
	        
	        userOp.get().setVerificationToken(token);
	        userRepo.save(userOp.get());
	        emailHelper.sendResetPasswordEmail(email, token);
		}
	}

	public void handleResetPassword(String token, String newPassword) throws EmailVerificationException {
		logger.info("token " + token + " newPassword " + newPassword);
		
		if(token == null || newPassword == null) {
			throw new IllegalArgumentException();
		}
		
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

	public Page<User> searchGuides(ServiceType serviceType, City city, Language language, GroupSizeCategory groupSize,
			Gender gender, Boolean isLocalGuide, Boolean isInternationalGuide, Integer minRating, Pageable pageable) {
		
		return userRepo.findGuideByFilter(serviceType, city, language, groupSize, gender, isLocalGuide, isInternationalGuide, minRating, pageable);

	}
	
	
	public User getUserDetails(Long userId) throws Exception {
	    User user = userRepo.findById(userId).orElseThrow(() -> new Exception("User not found"));
	    Set<City> cities = new HashSet<>();
	    Set<ServiceType> types = new HashSet<>();
	    Set<Language> languages = new HashSet<>();
	    Set<GroupSizeCategory> groupSizes = new HashSet<>();
	    for (GuideService g : user.getGuideServices()) {
	        cities.add(g.getCity());
	        types.add(g.getType());
	        languages.add(g.getLanguage());
	        groupSizes.add(g.getGroupSizeCategory());
	    }
	    user.setCities(cities);
	    user.setServiceTypes(types);
	    user.setLanguages(languages);
	    user.setGroupSizes(groupSizes);
	    return user;
	}
}
