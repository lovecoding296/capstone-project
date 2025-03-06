package com.tourapp.service;

import com.tourapp.entity.Account;
import com.tourapp.entity.Role;
import com.tourapp.entity.TourGuide;
import com.tourapp.entity.Tourist;
import com.tourapp.repository.AccountRepository;
import com.tourapp.repository.TourGuideRepository;
import com.tourapp.repository.TouristRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
	
	@Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TouristRepository touristRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TourGuideRepository tourGuideRepository;

    @Transactional
    public Account registerTourist(Account account) {
        if (accountRepository.existsByEmail(account.getEmail())) {
            throw new IllegalArgumentException("Email already exists");       }
        
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setRole(Role.ROLE_TOURIST);
        account = accountRepository.save(account);
        
        Tourist tourist = new Tourist();
        tourist.setAccount(account);
        touristRepository.save(tourist);
        
        return account;
    }
    
    @Transactional
    public Account registerTourGuide(Account account) {
        if (accountRepository.existsByEmail(account.getEmail())) {
            throw new IllegalArgumentException("Email already exists");       }
        
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setRole(Role.ROLE_TOURGIDE);
        account = accountRepository.save(account);
        
        TourGuide tourGuide = new TourGuide();
        tourGuide.setAccount(account);
        tourGuideRepository.save(tourGuide);
        
        return account;
    }
}

