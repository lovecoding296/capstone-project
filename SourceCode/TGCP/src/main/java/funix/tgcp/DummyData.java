package funix.tgcp;

import java.util.Optional;
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
import funix.tgcp.guide.service.GuideServiceService;
import funix.tgcp.guide.service.ServiceType;

@Component
public class DummyData implements ApplicationRunner {

	@Autowired
    private UserRepository userRepo;
	
	@Autowired
    private GuideRequestRepository guideRequestRepo;
	
	@Autowired
    private GuideServiceService guideServiceService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

	@Override
	public void run(ApplicationArguments args) throws Exception {

        
        createUser("admin@tgcp.com", "admin", "123", Role.ROLE_ADMIN);
        createUser("giang@tgcp.com", "giang", "123", Role.ROLE_USER);
        createUser("phi@tgcp.com",   "phi",   "123", Role.ROLE_USER);
        
        createUser("duc@tgcp.com",   	"Nguyễn Văn Đức",   "123", Role.ROLE_GUIDE,  	88,  4.8, true,  false, "/uploads/guide1.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("linh@tgcp.com",  	"Trần Thị Linh",  	"123", Role.ROLE_GUIDE,  	150, 4.7, true,  false, "/uploads/guide2.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("minh@tgcp.com",  	"Lê Văn Minh",  	"123", Role.ROLE_GUIDE,   	32,  5,   true,  false, "/uploads/guide3.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("huy@tgcp.com",  	"Nguyễn Hữu Huy",  	"123", Role.ROLE_GUIDE,   	5,   4,   true,  false, "/uploads/guide4.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("thao@tgcp.com",  	"Phạm Thị Thảo",  	"123", Role.ROLE_GUIDE,  	15,  4.6, true,  true,  "/uploads/guide5.webp", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("tuan@tgcp.com",  	"Đỗ Tuấn Anh",  	"123", Role.ROLE_GUIDE,  	100, 5,   true,  true,  "/uploads/guide6.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("nga@tgcp.com",  	"Bùi Thị Nga",  	"123", Role.ROLE_GUIDE,  	40,  5,   true,  false, "/uploads/guide1.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("khanh@tgcp.com",  	"Trịnh Toàn Khánh", "123", Role.ROLE_GUIDE,   	60,  5,   true,  true,  "/uploads/guide2.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("thu@tgcp.com",  	"Đặng Thị Thu",  	"123", Role.ROLE_GUIDE,   	100, 3.9, false, true,  "/uploads/guide3.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("long@tgcp.com",  	"Hồ Hoàng Long",  	"123", Role.ROLE_GUIDE,  	70,  4.8, true,  false, "/uploads/guide4.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("anh@tgcp.com", 		"Nguyễn Thị Ánh", 	"123",  Role.ROLE_GUIDE,   	100, 4.6, false, true,  "/uploads/guide5.webp", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("bao@tgcp.com", 		"Lý Văn Bảo", 		"123", Role.ROLE_GUIDE,  	80,  4.7, true,  true,  "/uploads/guide6.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("trang@tgcp.com", 	"Mai Thị Trang", 	"123", Role.ROLE_GUIDE,   	99,  4.9, false, true,  "/uploads/guide1.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("khoa@tgcp.com", 	"Vũ Ngọc Khoa", 	"123", Role.ROLE_GUIDE,   	32,  4.2, true,  true,  "/uploads/guide2.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("tien@tgcp.com", 	"Cao Minh Tiến", 	"123", Role.ROLE_GUIDE,   	11,  4.8, true,  true,  "/uploads/guide3.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("hien@tgcp.com", 	"Đinh Thị Hiền", 	"123", Role.ROLE_GUIDE,  	5,   4.9, true,  true,  "/uploads/guide4.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("son@tgcp.com", 		"Tạ Quốc Sơn", 		"123", Role.ROLE_GUIDE,  	13,  4.8, true,  true,  "/uploads/guide5.webp", "Vietcombank", "Nguyen Van A", "0123456789123");
        createUser("nhu@tgcp.com", 		"Lâm Thị Như", 		"123", Role.ROLE_GUIDE,   	25,  4.9, true,  true,  "/uploads/guide6.jpg", "Vietcombank", "Nguyen Van A", "0123456789123");
	}
	
	private void createService(User u, City c, Language l, ServiceType t, GroupSizeCategory g, Double price) {
		GuideService gs = new GuideService();
		
		gs.setCity(c);
		gs.setLanguage(l);
		gs.setGroupSizeCategory(g);
		gs.setType(t);
		gs.setPricePerDay(price);
		gs.setGuide(u);
		
		guideServiceService.createGuideService(gs);
				
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
            user.setReviewCount(reviewCount);
            user.setAverageRating(rating);
            
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
            
            	
            
            createService(savedU, City.HA_NOI, Language.English, ServiceType.TRANSLATOR, GroupSizeCategory.UNDER_5, 800000d);
            createService(savedU, City.HA_NOI, Language.English, ServiceType.TRANSLATOR, GroupSizeCategory.FROM_5_TO_10, 1000000d);            
            createService(savedU, City.HA_NOI, Language.English, ServiceType.TRANSLATOR, GroupSizeCategory.OVER_10, 1200000d);
            
            createService(savedU, City.HA_NOI, Language.French, ServiceType.TRANSLATOR, GroupSizeCategory.UNDER_5, 950000d);
            createService(savedU, City.HA_NOI, Language.French, ServiceType.TRANSLATOR, GroupSizeCategory.FROM_5_TO_10, 1200000d);
            createService(savedU, City.HA_NOI, Language.French, ServiceType.TRANSLATOR, GroupSizeCategory.OVER_10, 1500000d);
            
            createService(savedU, City.HAI_PHONG, Language.English, ServiceType.TRANSLATOR, GroupSizeCategory.UNDER_5, 950000d);
            createService(savedU, City.HAI_PHONG, Language.English, ServiceType.TRANSLATOR, GroupSizeCategory.FROM_5_TO_10, 1000000d);            
            createService(savedU, City.HAI_PHONG, Language.English, ServiceType.TRANSLATOR, GroupSizeCategory.OVER_10, 1200000d);
            
            createService(savedU, City.HAI_PHONG, Language.French, ServiceType.TRANSLATOR, GroupSizeCategory.UNDER_5, 950000d);
            createService(savedU, City.HAI_PHONG, Language.French, ServiceType.TRANSLATOR, GroupSizeCategory.FROM_5_TO_10, 1000000d);
            createService(savedU, City.HAI_PHONG, Language.French, ServiceType.TRANSLATOR, GroupSizeCategory.OVER_10, 1150000d);
            
            createService(savedU, City.HA_NOI, Language.English, ServiceType.CITY_TOUR, GroupSizeCategory.UNDER_5, 950000d);
            createService(savedU, City.HA_NOI, Language.English, ServiceType.CITY_TOUR, GroupSizeCategory.FROM_5_TO_10, 1300000d);
            createService(savedU, City.HA_NOI, Language.English, ServiceType.CITY_TOUR, GroupSizeCategory.OVER_10, 1600000d);
            
            createService(savedU, City.HA_NOI, Language.French, ServiceType.CITY_TOUR, GroupSizeCategory.UNDER_5, 950000d);
            createService(savedU, City.HA_NOI, Language.French, ServiceType.CITY_TOUR, GroupSizeCategory.FROM_5_TO_10, 1000000d);
            createService(savedU, City.HA_NOI, Language.French, ServiceType.CITY_TOUR, GroupSizeCategory.OVER_10, 1550000d);
            
            
            System.out.println("account created successfully!");
        } else {
            System.out.println("account already exists.");
        }
    }
	
	

}
