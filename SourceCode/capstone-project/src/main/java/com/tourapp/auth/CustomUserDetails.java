package com.tourapp.auth;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.tourapp.entity.AppUser;
import com.tourapp.entity.Role;

public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	CustomUserDetails(AppUser user){
		this.user = user;
	}
	
	private final AppUser user;
	
	public Long getId() {
		return user.getId();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().toString()));
	}

	@Override
	public boolean isEnabled() {
		return user.isVerified(); // Chặn login nếu chưa xác minh
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
		if(user.getRole() == Role.ROLE_GUIDE || user.getRole() == Role.ROLE_AGENCY) {
			return user.isKycApproved();
		}
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