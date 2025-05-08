package funix.tgcp.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	        @RequestParam(required = false) Boolean kycApproved, 
	        @RequestParam(required = false) Boolean enabled,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {	    

		Pageable pageable = PageRequest.of(page, size);
		return userService.findUserByFilter(email, fullName, role, kycApproved, enabled, pageable);
	}

	
	
	@PutMapping("/{id}/enable")
	public ResponseEntity<?> enableUser(@PathVariable Long id) {
	    logger.info("Request to enable user with id: {}", id);

	    try {
	        userService.setUserEnabled(id, true);
	        logger.info("User with id: {} successfully enabled.", id);
	        return ResponseEntity.status(HttpStatus.OK).body("User enabled successfully.");
	    } catch (Exception e) {
	        logger.error("Error enabling user with id: {}", id, e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error enabling user.");
	    }
	}

	@PutMapping("/{id}/disable")
	public ResponseEntity<?> disableUser(@PathVariable Long id) {
	    logger.info("Request to disable user with id: {}", id);

	    try {
	        userService.setUserEnabled(id, false);
	        logger.info("User with id: {} successfully disabled.", id);
	        return ResponseEntity.status(HttpStatus.OK).body("User disabled successfully.");
	    } catch (Exception e) {
	        logger.error("Error disabling user with id: {}", id, e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error disabling user.");
	    }
	}


	@PutMapping("/{id}/approve-kyc")
	public ResponseEntity<?> approveKyc(@PathVariable Long id) {
	    logger.info("Request to approve KYC for user with id: {}", id);

	    try {
	        userService.approveKyc(id);
	        logger.info("KYC successfully approved for user with id: {}", id);
	        return ResponseEntity.status(HttpStatus.OK).body("KYC approved successfully.");
	    } catch (Exception e) {
	        logger.error("Error approving KYC for user with id: {}", id, e);
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error approving KYC.");
	    }
	}

	@PutMapping("/{id}/reject-kyc")
	public ResponseEntity<?> rejectKyc(@PathVariable Long id, @RequestParam String reason) {
	    logger.info("Request to reject KYC for user with id: {} with reason: {}", id, reason);

	    try {
	        userService.rejectKyc(id, reason);
	        logger.info("KYC successfully rejected for user with id: {}", id);
	        return ResponseEntity.status(HttpStatus.OK).body("KYC rejected successfully.");
	    } catch (Exception e) {
	        logger.error("Error rejecting KYC for user with id: {}", id, e);
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error rejecting KYC.");
	    }
	}

	
}
