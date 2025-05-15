package funix.tgcp.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.util.LogHelper;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

	private static final LogHelper logger = new LogHelper(UserRestController.class);

	
	@Autowired
	private UserService userService;

	// Tìm người dùng theo email
	@GetMapping("/by-email")
	public ResponseEntity<User> findUserByEmail(@RequestParam String email) {
		Optional<User> user = userService.findByEmail(email);
		return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	
	@GetMapping("/{id}")
	public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
		
		Optional<User> user = userService.findById(id);
		if (user.isPresent() ) {
			return ResponseEntity.ok(user.get());			
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	}
	
	@GetMapping()
	public Page<User> findUserByFilter(
	        @RequestParam(required = false) String email,
	        @RequestParam(required = false) String fullName,
	        @RequestParam(required = false) Role role, 
	        @RequestParam(required = false) Boolean enabled,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {	    

		Pageable pageable = PageRequest.of(page, size);
		return userService.findUserByFilter(email, fullName, role, enabled, pageable);
	}
	
	
	@PutMapping("/{id}/enable")
	public ResponseEntity<?> enableUser(@PathVariable Long id) {
		logger.info("Request to enable user with id: {}", id);

		userService.setUserEnabled(id, true);
		logger.info("User with id: {} successfully enabled.", id);
		return ResponseEntity.status(HttpStatus.OK).body("User enabled successfully.");
	}

	@PutMapping("/{id}/disable")
	public ResponseEntity<?> disableUser(@PathVariable Long id) {
		logger.info("Request to disable user with id: {}", id);
		userService.setUserEnabled(id, false);
		logger.info("User with id: {} successfully disabled.", id);
		return ResponseEntity.status(HttpStatus.OK).body("User disabled successfully.");
	}

}
