package funix.tgcp.user.request;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.exception.EmailAlreadyExistsException;
import funix.tgcp.exception.EmailVerificationException;
import funix.tgcp.notification.NotificationService;
import funix.tgcp.user.User;
import funix.tgcp.user.UserRepository;
import funix.tgcp.util.EmailHelper;
import funix.tgcp.util.FileHelper;
import jakarta.validation.Valid;

@Service
public class UserRequestService {
	
    @Autowired
    private UserRepository userRepo;

	@Autowired
	private UserRequestRepository userRequestRepo;
	
    @Autowired
    private EmailHelper emailHelper;
	
    @Autowired
	private FileHelper fileHelper;
    
    @Autowired
	private PasswordEncoder passwordEncoder;
    
    @Autowired 
    private NotificationService notiService;
	
	@Transactional
    public UserRequest registerUser(MultipartFile cccdFile, @Valid UserRequest userRequest) throws EmailAlreadyExistsException, IOException {
		if (userRepo.existsByEmail(userRequest.getEmail())) {
			System.out.println("Attempt to register with existing email: " + userRequest.getEmail());
			throw new EmailAlreadyExistsException("Email already exists");
		}
		
		String cccd = fileHelper.uploadFile(cccdFile);			
		if(cccd != null) {
			userRequest.setCccd(cccd);
    	}
		
		// Tạo token ngẫu nhiên
        String token = UUID.randomUUID().toString();
        System.out.println("Tạo token ngẫu nhiên " + token);
        
        userRequest.setVerificationToken(token);
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		
		emailHelper.sendVerificationEmail(userRequest.getEmail(), token);		
				
		UserRequest savedUser = userRequestRepo.save(userRequest);
		return savedUser;
	}
	
	@Transactional
	public void verifyEmail(String token) throws EmailVerificationException{
		Optional<UserRequest> optionalUserRequest = userRequestRepo.findByVerificationToken(token);
        if (optionalUserRequest.isPresent()) {
        	UserRequest userRequest = optionalUserRequest.get();

        	User user = new User();
        	user.setPassword(userRequest.getPassword());
        	user.setCccd(userRequest.getCccd());
        	user.setEmail(userRequest.getEmail());
        	user.setAvatarUrl("/uploads/default-cccd.jpg");
        	user.setFullName(userRequest.getFullName());        	
        	
            userRequestRepo.delete(userRequest);            
            userRepo.save(user);            
            notiService.sendNotificationToAdmin(
            		"New user registered, please check kyc!", 
            		"/admin/dashboard#manage-users");
        } else {
            throw new EmailVerificationException("Invalid verification token or the token has expired.");
        }
	}
}
