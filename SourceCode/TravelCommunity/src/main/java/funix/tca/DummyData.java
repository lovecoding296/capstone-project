package funix.tca;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import funix.tca.entity.AppUser;
import funix.tca.entity.Post;
import funix.tca.entity.Role;
import funix.tca.repository.AppUserRepository;
import funix.tca.repository.PostRepository;

@Component
public class DummyData implements ApplicationRunner {

	@Autowired
    private AppUserRepository userRepository;
	
	@Autowired
    private PostRepository postRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

	@Override
	public void run(ApplicationArguments args) throws Exception {

        
        createUser("admin@tca.com", "123", Role.ROLE_ADMIN);
        createUser("phi@tca.com", "123", Role.ROLE_USER);
        createUser("giang@tca.com", "123", Role.ROLE_USER);
        createUser("duc@tca.com", "123", Role.ROLE_USER);
        
        
        
	}
	
	private void createUser(String email, String rawPassword, Role role) {
        Optional<AppUser> adminUser = userRepository.findByEmail(email);

        if (adminUser.isEmpty()) {
            AppUser admin = new AppUser();
            admin.setFullName("Administrator");
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(rawPassword)); // Mật khẩu mã hóa
            admin.setRole(role);

            userRepository.save(admin);
            System.out.println("Admin account created successfully!");
        } else {
            System.out.println("Admin account already exists.");
        }
    }

}
