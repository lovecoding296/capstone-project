package funix.tca.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import funix.tca.entity.AppUser;
import funix.tca.exception.EmailVerificationException;
import funix.tca.repository.AppUserRepository;
import jakarta.validation.Valid;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;
    
    @Autowired
	private PasswordEncoder passwordEncoder;

    // Lưu một AppUser mới
    public AppUser save(AppUser appUser) {
    	appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return appUserRepository.save(appUser);
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

	public void verifyEmail(String token) throws EmailVerificationException{
		// TODO Auto-generated method stub
		
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
