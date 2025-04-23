package funix.tgcp.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;import funix.tgcp.user.Language;
import funix.tgcp.user.User;

import java.util.Collections;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	public CustomUserDetails(User user){
		this.user = user;
	}
	
	private final User user;
	
	public Long getId() {
		return user.getId();
	}
	
	public boolean isAdmin() {
		return user.isAdmin();
	}
	
	public User getUser() {
		return user;
	}
	
	public String getFullName() {
		return user.getFullName();
	}
	
	public Set<Language> getLanguages() {
		return user.getLanguages();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().toString()));
	}

	@Override
	public boolean isEnabled() {
		return user.isVerified(); // Chặn login nếu chưa xác minh
//		return true;
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		 
		return user.isKycApproved();
		
//		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}
	
	// New method to get the current authenticated user
    public static CustomUserDetails getCurrentUserDetails() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (principal instanceof CustomUserDetails) {
            return (CustomUserDetails) principal;
        }

        return null; // Return null if user is not authenticated or principal is not an instance of CustomUserDetails
    }
}