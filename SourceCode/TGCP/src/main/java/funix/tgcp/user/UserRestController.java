package funix.tgcp.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

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
	 
}
