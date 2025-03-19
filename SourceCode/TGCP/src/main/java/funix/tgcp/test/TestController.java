package funix.tgcp.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.user.UserService;

@RestController
@RequestMapping("/api")
public class TestController {

	
	@Autowired
	private UserService userService;
	
	
	@PostMapping(value = "/login")
	public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password){
		System.out.println("email " + email);
		System.out.println("pass" + password);
		
//		Optional<User> user = userService.findByEmail(email);
//		if(user.isPresent() && user.get().getPassword().equals(password)) {
//			System.out.println("password ok");
//		}
		
		
		
		return ResponseEntity.status(HttpStatus.OK).body("Ok");
	}
	
	@GetMapping(value = "/test")
	public ResponseEntity<String> test(){
		
		
//		Optional<User> user = userService.findByEmail(email);
//		if(user.isPresent() && user.get().getPassword().equals(password)) {
//			System.out.println("password ok");
		
		
//		}
				
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof CustomUserDetails) {
		    CustomUserDetails userDetails = (CustomUserDetails) principal;
			Long userId = userDetails.getId();
			
			System.out.println("user logged in: id " + userId + " " + userDetails.getUsername() + " " + userDetails.getPassword());
		} else {
			System.out.println("principal " + (String)principal);
		    System.out.println("Principal is not an instance of CustomUserDetails");
		}
		
		
		
		
		return ResponseEntity.status(HttpStatus.OK).body("Ok");
	}
}
