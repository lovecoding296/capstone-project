package com.tourapp.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.tourapp.entity.Account;
import com.tourapp.entity.TourGuide;
import com.tourapp.entity.Tourist;
import com.tourapp.repository.AccountRepository;
import com.tourapp.repository.TourGuideRepository;
import com.tourapp.repository.TouristRepository;

import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final TouristRepository touristRepository;
	private final TourGuideRepository tourGuideRepository;
	private final AccountRepository accountRepository;
	private final HttpSession session;

	public CustomUserDetailsService(TouristRepository touristRepository, TourGuideRepository tourGuideRepository,
			AccountRepository accountRepository, HttpSession session) {
		this.touristRepository = touristRepository;
		this.tourGuideRepository = tourGuideRepository;
		this.session = session;
		this.accountRepository = accountRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		Account account = accountRepository.findByEmail(email);
		if(account != null) {
			System.out.println("Login successful");
			session.setAttribute("USER_SESSION", email);
			return new User(
					account.getEmail(),
					account.getPassword(),
	                Collections.singletonList(() -> account.getRole().toString())
	            ); 
		}

		throw new UsernameNotFoundException("User not found");
	}
}
