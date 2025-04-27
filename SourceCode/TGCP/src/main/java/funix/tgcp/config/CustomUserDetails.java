package funix.tgcp.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import funix.tgcp.user.User;

import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


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
}