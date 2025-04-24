package funix.tgcp;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import funix.tgcp.user.User;
import funix.tgcp.user.UserRepository;
import funix.tgcp.user.City;
import funix.tgcp.user.Language;
import funix.tgcp.user.Role;
import funix.tgcp.post.PostRepository;

@Component
public class DummyData implements ApplicationRunner {

	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private PostRepository postRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

	@Override
	public void run(ApplicationArguments args) throws Exception {

        
        createUser("admin@tgcp.com", "admin", "123", Role.ROLE_ADMIN);
        createUser("giang@tgcp.com", "giang", "123", Role.ROLE_USER);
        createUser("duc@tgcp.com",   "duc",   "123", Role.ROLE_USER);
        
        createUser("phi@tgcp.com",   "phi",   "123", Role.ROLE_GUIDE, City.HAI_PHONG,  88,  4.8, true,  false, "/uploads/guide1.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi1@tgcp.com",  "phi1",  "123", Role.ROLE_GUIDE, City.HAI_PHONG,  150, 4.7, true,  false, "/uploads/guide2.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi2@tgcp.com",  "phi2",  "123", Role.ROLE_GUIDE, City.HAI_DUONG,  32,  5,   true,  false, "/uploads/guide3.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi3@tgcp.com",  "phi3",  "123", Role.ROLE_GUIDE, City.HA_NOI,     5,   4,   true,  false, "/uploads/guide4.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi4@tgcp.com",  "phi4",  "123", Role.ROLE_GUIDE, City.BAC_GIANG,  15,  4.6, true,  true,  "/uploads/guide5.webp", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi5@tgcp.com",  "phi5",  "123", Role.ROLE_GUIDE, City.BAC_NINH,   100, 5,   true,  true,  "/uploads/guide6.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi6@tgcp.com",  "phi6",  "123", Role.ROLE_GUIDE, City.BINH_PHUOC, 40,  5,   true,  false, "/uploads/guide1.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi7@tgcp.com",  "phi7",  "123", Role.ROLE_GUIDE, City.HAI_DUONG,  60,  5,   true,  true,  "/uploads/guide2.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi8@tgcp.com",  "phi8",  "123", Role.ROLE_GUIDE, City.HAI_DUONG,  100, 3.9, false, true,  "/uploads/guide3.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi9@tgcp.com",  "phi9",  "123", Role.ROLE_GUIDE, City.BINH_THUAN, 70,  4.8, true,  false, "/uploads/guide4.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi10@tgcp.com", "ph10i", "123", Role.ROLE_GUIDE, City.HAI_DUONG,  100, 4.6, false, true,  "/uploads/guide5.webp", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi11@tgcp.com", "phi11", "123", Role.ROLE_GUIDE, City.HAI_DUONG,  80,  4.7, true,  true,  "/uploads/guide6.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi12@tgcp.com", "phi12", "123", Role.ROLE_GUIDE, City.HAI_DUONG,  99,  4.9, false, true,  "/uploads/guide1.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi13@tgcp.com", "phi13", "123", Role.ROLE_GUIDE, City.HAI_DUONG,  32,  4.2, true,  true,  "/uploads/guide2.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi14@tgcp.com", "phi14", "123", Role.ROLE_GUIDE, City.DIEN_BIEN,  11,  4.8, true,  true,  "/uploads/guide3.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi15@tgcp.com", "phi15", "123", Role.ROLE_GUIDE, City.HAI_DUONG,  5,   4.9, true,  true,  "/uploads/guide4.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi16@tgcp.com", "phi16", "123", Role.ROLE_GUIDE, City.HAI_DUONG,  13,  4.8, true,  true,  "/uploads/guide5.webp", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi17@tgcp.com", "phi17", "123", Role.ROLE_GUIDE, City.HAI_DUONG,  25,  4.9, true,  true,  "/uploads/guide6.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
	}
	
	private void createUser(String email, String fullName, String rawPassword, Role role) {
        Optional<User> userOp = userRepository.findByEmail(email);

        if (userOp.isEmpty()) {
            User user = new User();
            user.setFullName("Administrator");
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword)); // Mật khẩu mã hóa
            user.setRole(role);
            user.setFullName(fullName);
            user.setAvatarUrl("/uploads/default-avatar.jpg");
            user.setLanguages(new HashSet<>(Set.of(Language.Vietnamese, Language.English)));
            user.setKycApproved(true);
            user.setVerified(true);
            
            userRepository.save(user);
            System.out.println("account created successfully!");
        } else {
            System.out.println("account already exists.");
        }
    }
	
	private void createUser(String email
			, String fullName
			, String rawPassword
			, Role role
			, City city
			, int reviewCount
			, double rating
			, boolean isLocalGuide
			, boolean isInternationalGuidem
			, String avtarUrl
			, String bankName
			, String accountHodler
			, String accountNumber
			) {
        Optional<User> userOp = userRepository.findByEmail(email);

        if (userOp.isEmpty()) {
            User user = new User();
            user.setFullName("Administrator");
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword)); // Mật khẩu mã hóa
            user.setRole(role);
            user.setFullName(fullName);
            user.setAvatarUrl("/uploads/default-avatar.jpg");
            user.setLanguages(new HashSet<>(Set.of(Language.Vietnamese, Language.English)));
            user.setKycApproved(true);
            user.setVerified(true);
            user.setCity(city);
            user.setReviewCount(reviewCount);
            user.setAverageRating(rating);
            
            user.setActive(true);
            user.setVerified(true);
            user.setKycApproved(true);
            user.setInternationalGuide(isInternationalGuidem);
            user.setLocalGuide(isLocalGuide);
            user.setAvatarUrl(avtarUrl);
            
            user.setAccountHolder(accountHodler);
            user.setBankName(bankName);
            user.setAccountNumber(accountNumber);
            
            userRepository.save(user);
            System.out.println("account created successfully!");
        } else {
            System.out.println("account already exists.");
        }
    }
	
	

}
