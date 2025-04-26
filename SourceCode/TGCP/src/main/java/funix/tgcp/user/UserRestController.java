package funix.tgcp.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import funix.tgcp.booking.BookingRestController;
import funix.tgcp.util.LogHelper;

import java.util.List;
import java.util.Optional;

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
	        @RequestParam(required = false) Boolean kycApproved, 
	        @RequestParam(required = false) Boolean enabled,
	        @RequestParam(required = false) Boolean verified,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {	    

		Pageable pageable = PageRequest.of(page, size);
		return userService.findUserByFilter(email, fullName, role, kycApproved, enabled, verified, pageable);
	}

	
	
	@PutMapping("/{id}/enable")
	public ResponseEntity<?> enableUser(@PathVariable Long id) {
		userService.setUserEnabled(id, true);
		return ResponseEntity.ok("User enabled");
	}

	@PutMapping("/{id}/disable")
	public ResponseEntity<?> disableUser(@PathVariable Long id) {
		userService.setUserEnabled(id, false);
		return ResponseEntity.ok("User disabled");
	}

	@PutMapping("/{id}/approve-kyc")
	public ResponseEntity<?> approveKyc(@PathVariable Long id) {
		userService.approveKyc(id);
		return ResponseEntity.ok("KYC approved");
	}
}
