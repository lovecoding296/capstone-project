package funix.tca.appuser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import funix.tca.exception.EmailAlreadyExistsException;
import funix.tca.exception.EmailVerificationException;
import funix.tca.util.EmailHelper;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;
    
    @Autowired
	private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailHelper emailHelper;
    

    // Lưu một AppUser mới
    public AppUser save(AppUser appUser) {
    	if (appUser.getAvatarUrl() == null || appUser.getAvatarUrl().isBlank()) {
    	    appUser.setAvatarUrl("/uploads/default-avatar.jpg");
    	}
    	appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return appUserRepository.save(appUser);
    }
    
    public List<AppUser> getUnapprovedUsers() {
        return appUserRepository.findByKycApprovedFalse();
    }

    // Tìm AppUser theo ID
    public Optional<AppUser> findById(Long id) {
        return appUserRepository.findById(id);
    }

    // Tìm AppUser theo email
    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    // Lấy danh sách tất cả AppUser
    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }

    // Cập nhật thông tin AppUser
    public AppUser update(AppUser userToUpdate) {
        return appUserRepository.save(userToUpdate);
    }

    // Xóa AppUser theo ID
    public void deleteById(Long id) {
        appUserRepository.deleteById(id);
    }
    
    public AppUser registerUser(AppUser user) throws EmailAlreadyExistsException {
		if (appUserRepository.existsByEmail(user.getEmail())) {
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

		AppUser savedUser = appUserRepository.save(user);
		return savedUser;
	}

	public void verifyEmail(String token) throws EmailVerificationException{
		Optional<AppUser> optionalUser = appUserRepository.findByVerificationToken(token);

        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();
            user.setVerified(true);
            user.setVerificationToken(null); // Xóa token sau khi xác thực
            appUserRepository.save(user);
        } else {
            throw new EmailVerificationException("Invalid verification token or the token has expired.");
        }
	}
	
	public boolean approveAppUser(Long id) {
		Optional<AppUser> currentUserOption = findById(id);

		if (currentUserOption.isPresent()) {
			AppUser currentUser = currentUserOption.get();
			currentUser.setKycApproved(true);
			appUserRepository.save(currentUser);
			return true;
		}
		return false;		
	}
	
	public boolean rejectAppUser(Long id) {
		Optional<AppUser> currentUserOption = findById(id);

		if (currentUserOption.isPresent()) {
			AppUser currentUser = currentUserOption.get();
			currentUser.setKycApproved(false);
			appUserRepository.save(currentUser);
			return true;
		}
		return false;		
	}
	
	public void updateCurrentUser(AppUser user) {

		Optional<AppUser> currentUserOption = findById(user.getId());

		if (currentUserOption.isEmpty()) {
			throw new IllegalArgumentException("Updated user information cannot be null.");
		}
		
		AppUser currentUser = currentUserOption.get();

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
		if (user.getCity() != null && !user.getCity().isEmpty()) {
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
		if (user.getHomeAddress() != null && !user.getHomeAddress().isEmpty()) {
			currentUser.setHomeAddress(user.getHomeAddress());
		}
		if (user.getFamilyPhone() != null && !user.getFamilyPhone().isEmpty()) {
			currentUser.setFamilyPhone(user.getFamilyPhone());
		}
		if (user.getAge() != 0) {
			currentUser.setAge(user.getAge());
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
		

		appUserRepository.save(currentUser);
	}	
}
