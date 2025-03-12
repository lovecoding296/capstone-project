package funix.tca.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import funix.tca.entity.AppUser;
import funix.tca.exception.EmailVerificationException;
import funix.tca.repository.AppUserRepository;

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
    public AppUser update(AppUser appUser) {
    	appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return appUserRepository.save(appUser);
    }

    // Xóa AppUser theo ID
    public void deleteById(Long id) {
        appUserRepository.deleteById(id);
    }

	public void verifyEmail(String token) throws EmailVerificationException{
		// TODO Auto-generated method stub
		
	}
}
