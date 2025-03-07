package com.tourapp.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.tourapp.entity.AppUser;
import com.tourapp.repository.UserRepository;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository accountRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		AppUser user = accountRepository.findByEmail(email);
		if(user != null) {
			return new User(
					user.getEmail(),
					user.getPassword(),
	                Collections.singletonList(() -> user.getRole().toString())
	            ); 
		}

		throw new UsernameNotFoundException("User not found");
	}
}
