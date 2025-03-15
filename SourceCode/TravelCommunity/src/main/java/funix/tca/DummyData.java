package funix.tca;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import funix.tca.appuser.AppUser;
import funix.tca.appuser.AppUserRepository;
import funix.tca.appuser.Language;
import funix.tca.appuser.Role;
import funix.tca.post.PostRepository;

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

        
        createUser("admin@tca.com", "admin", "123", Role.ROLE_ADMIN);
        createUser("phi@tca.com", "phi", "123", Role.ROLE_USER);
        createUser("giang@tca.com", "giang", "123", Role.ROLE_USER);
        createUser("duc@tca.com", "duc", "123", Role.ROLE_USER);
        
        
        
	}
	
	private void createUser(String email, String fullName, String rawPassword, Role role) {
        Optional<AppUser> userOp = userRepository.findByEmail(email);

        if (userOp.isEmpty()) {
            AppUser user = new AppUser();
            user.setFullName("Administrator");
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword)); // Mật khẩu mã hóa
            user.setRole(role);
            user.setFullName(fullName);
            user.setAvatarUrl("/uploads/default-avatar.jpg");
            user.setLanguages(new HashSet<>(Set.of(Language.Vietnamese, Language.English)));
            userRepository.save(user);
            System.out.println("account created successfully!");
        } else {
            System.out.println("account already exists.");
        }
    }
	

}
