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
import funix.tgcp.guide.request.GuideRequest;
import funix.tgcp.guide.request.GuideRequestRepository;
import funix.tgcp.guide.request.GuideRequestStatus;
import funix.tgcp.guide.service.GroupSizeCategory;
import funix.tgcp.guide.service.GuideService;
import funix.tgcp.guide.service.GuideServiceRepository;
import funix.tgcp.guide.service.ServiceType;
import funix.tgcp.post.PostRepository;

@Component
public class DummyData implements ApplicationRunner {

	@Autowired
    private UserRepository userRepo;
	
	@Autowired
    private GuideRequestRepository guideRequestRepo;
	
	@Autowired
    private GuideServiceRepository guideServiceRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

	@Override
	public void run(ApplicationArguments args) throws Exception {

        
        createUser("admin@tgcp.com", "admin", "123", Role.ROLE_ADMIN);
        createUser("giang@tgcp.com", "giang", "123", Role.ROLE_USER);
        createUser("duc@tgcp.com",   "duc",   "123", Role.ROLE_USER);
        
        createUser("phi@tgcp.com",   "phi",   "123", Role.ROLE_GUIDE,  	88,  4.8, true,  false, "/uploads/guide1.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi1@tgcp.com",  "phi1",  "123", Role.ROLE_GUIDE,  	150, 4.7, true,  false, "/uploads/guide2.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi2@tgcp.com",  "phi2",  "123", Role.ROLE_GUIDE,   32,  5,   true,  false, "/uploads/guide3.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi3@tgcp.com",  "phi3",  "123", Role.ROLE_GUIDE,   5,   4,   true,  false, "/uploads/guide4.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi4@tgcp.com",  "phi4",  "123", Role.ROLE_GUIDE,  	15,  4.6, true,  true,  "/uploads/guide5.webp", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi5@tgcp.com",  "phi5",  "123", Role.ROLE_GUIDE,   100, 5,   true,  true,  "/uploads/guide6.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi6@tgcp.com",  "phi6",  "123", Role.ROLE_GUIDE,  	40,  5,   true,  false, "/uploads/guide1.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi7@tgcp.com",  "phi7",  "123", Role.ROLE_GUIDE,   60,  5,   true,  true,  "/uploads/guide2.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi8@tgcp.com",  "phi8",  "123", Role.ROLE_GUIDE,   100, 3.9, false, true,  "/uploads/guide3.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi9@tgcp.com",  "phi9",  "123", Role.ROLE_GUIDE,  	70,  4.8, true,  false, "/uploads/guide4.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi10@tgcp.com", "ph10", "123",  Role.ROLE_GUIDE,   100, 4.6, false, true,  "/uploads/guide5.webp", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi11@tgcp.com", "phi11", "123", Role.ROLE_GUIDE,  	80,  4.7, true,  true,  "/uploads/guide6.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi12@tgcp.com", "phi12", "123", Role.ROLE_GUIDE,   99,  4.9, false, true,  "/uploads/guide1.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi13@tgcp.com", "phi13", "123", Role.ROLE_GUIDE,   32,  4.2, true,  true,  "/uploads/guide2.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi14@tgcp.com", "phi14", "123", Role.ROLE_GUIDE,   11,  4.8, true,  true,  "/uploads/guide3.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi15@tgcp.com", "phi15", "123", Role.ROLE_GUIDE,  	5,   4.9, true,  true,  "/uploads/guide4.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi16@tgcp.com", "phi16", "123", Role.ROLE_GUIDE,  	13,  4.8, true,  true,  "/uploads/guide5.webp", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("phi17@tgcp.com", "phi17", "123", Role.ROLE_GUIDE,   25,  4.9, true,  true,  "/uploads/guide6.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
	}
	
	private void createService(User u, City c, Language l, ServiceType t, GroupSizeCategory g, Double price) {
		GuideService gs = new GuideService();
		
		gs.setCity(c);
		gs.setLanguage(l);
		gs.setGroupSizeCategory(g);
		gs.setType(t);
		gs.setPrice(price);
		gs.setGuide(u);
		guideServiceRepo.save(gs);
		
	}
	
	private void createUser(String email, String fullName, String rawPassword, Role role) {
        Optional<User> userOp = userRepo.findByEmail(email);

        if (userOp.isEmpty()) {
            User user = new User();
            user.setFullName("Administrator");
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword)); // Mật khẩu mã hóa
            user.setRole(role);
            user.setFullName(fullName);
            user.setAvatarUrl("/uploads/default-avatar.jpg");
            user.setKycApproved(true);
            user.setVerified(true);
            
            
            userRepo.save(user);
            
            System.out.println("account created successfully!");
        } else {
            System.out.println("account already exists.");
        }
    }
	
	private void createUser(String email
			, String fullName
			, String rawPassword
			, Role role
			, int reviewCount
			, double rating
			, boolean isLocalGuide
			, boolean isInternationalGuidem
			, String avtarUrl
			, String bankName
			, String accountHodler
			, String accountNumber
			) {
        Optional<User> userOp = userRepo.findByEmail(email);

        if (userOp.isEmpty()) {
            User user = new User();
            user.setFullName("Administrator");
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword)); // Mật khẩu mã hóa
            user.setRole(role);
            user.setFullName(fullName);
            user.setAvatarUrl("/uploads/default-avatar.jpg");
            user.setKycApproved(true);
            user.setVerified(true);
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
            
            User savedU = userRepo.save(user);
            
            GuideRequest guideRequest = new GuideRequest();
            guideRequest.setUser(savedU);
            guideRequest.setInternationalGuide(isInternationalGuidem);
            guideRequest.setLocalGuide(isLocalGuide);
            guideRequest.setStatus(GuideRequestStatus.APPROVED);
            
            
            guideRequestRepo.save(guideRequest);
            
            	
            
            createService(savedU, City.HA_NOI, Language.English, ServiceType.TRANSLATOR, GroupSizeCategory.UNDER_5, 50d);
            createService(savedU, City.HA_NOI, Language.English, ServiceType.TRANSLATOR, GroupSizeCategory.UNDER_10, 55d);            
            createService(savedU, City.HA_NOI, Language.English, ServiceType.TRANSLATOR, GroupSizeCategory.OVER_10, 60d);
            
            createService(savedU, City.HA_NOI, Language.French, ServiceType.TRANSLATOR, GroupSizeCategory.UNDER_5, 70d);
            createService(savedU, City.HA_NOI, Language.French, ServiceType.TRANSLATOR, GroupSizeCategory.UNDER_10, 75d);
            createService(savedU, City.HA_NOI, Language.French, ServiceType.TRANSLATOR, GroupSizeCategory.OVER_10, 80d);
            
            createService(savedU, City.HAI_PHONG, Language.English, ServiceType.TRANSLATOR, GroupSizeCategory.UNDER_5, 50d);
            createService(savedU, City.HAI_PHONG, Language.English, ServiceType.TRANSLATOR, GroupSizeCategory.UNDER_10, 55d);            
            createService(savedU, City.HAI_PHONG, Language.English, ServiceType.TRANSLATOR, GroupSizeCategory.OVER_10, 60d);
            
            createService(savedU, City.HAI_PHONG, Language.French, ServiceType.TRANSLATOR, GroupSizeCategory.UNDER_5, 70d);
            createService(savedU, City.HAI_PHONG, Language.French, ServiceType.TRANSLATOR, GroupSizeCategory.UNDER_10, 75d);
            createService(savedU, City.HAI_PHONG, Language.French, ServiceType.TRANSLATOR, GroupSizeCategory.OVER_10, 80d);
            
            createService(savedU, City.HA_NOI, Language.English, ServiceType.CITY_TOUR, GroupSizeCategory.UNDER_5, 50d);
            createService(savedU, City.HA_NOI, Language.English, ServiceType.CITY_TOUR, GroupSizeCategory.UNDER_10, 55d);
            createService(savedU, City.HA_NOI, Language.English, ServiceType.CITY_TOUR, GroupSizeCategory.OVER_10, 60d);
            
            createService(savedU, City.HA_NOI, Language.French, ServiceType.CITY_TOUR, GroupSizeCategory.UNDER_10, 55d);
            createService(savedU, City.HA_NOI, Language.French, ServiceType.CITY_TOUR, GroupSizeCategory.UNDER_10, 60d);
            createService(savedU, City.HA_NOI, Language.French, ServiceType.CITY_TOUR, GroupSizeCategory.UNDER_10, 70d);
            
            
            System.out.println("account created successfully!");
        } else {
            System.out.println("account already exists.");
        }
    }
	
	

}
