package funix.tca.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import funix.tca.appuser.AppUser;

import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	CustomUserDetails(AppUser user){
		this.user = user;
	}
	
	private final AppUser user;
	
	public Long getId() {
		return user.getId();
	}
	
	public AppUser getAppUser() {
		return user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().toString()));
	}

	@Override
	public boolean isEnabled() {
//		return user.isVerified(); // Chặn login nếu chưa xác minh
		return true;
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
		 
//		return user.isKycApproved();
		
		return true;
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