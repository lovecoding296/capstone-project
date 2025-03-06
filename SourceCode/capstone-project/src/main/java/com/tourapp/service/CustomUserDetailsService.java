package com.tourapp.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.tourapp.entity.TourGuide;
import com.tourapp.entity.Tourist;
import com.tourapp.repository.TourGuideRepository;
import com.tourapp.repository.TouristRepository;

import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final TouristRepository touristRepository;
    private final TourGuideRepository tourGuideRepository;
    private final HttpSession session;

    public CustomUserDetailsService(TouristRepository touristRepository, TourGuideRepository tourGuideRepository, HttpSession session) {
        this.touristRepository = touristRepository;
        this.tourGuideRepository = tourGuideRepository;
		this.session = session;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Kiểm tra trong bảng Tourist trước
//    	System.out.println("loadUserByUsername " + email);
//        Tourist tourist = touristRepository.findByEmail(email);
//        if (tourist != null) {
//        	session.setAttribute("loggedInUser", tourist);
//            return new User(
//                tourist.getEmail(),
//                tourist.getPassword(),
//                Collections.singletonList(() -> "ROLE_TOURIST") // Gán quyền cho Tourist
//            );
//        } else {
//        	System.out.println("tourist null");
//        }
//
//        // Nếu không phải Tourist, kiểm tra trong TourGuide
//        TourGuide tourGuide = tourGuideRepository.findByEmail(email);
//        if (tourGuide != null) {
//        	session.setAttribute("loggedInUser", tourGuide);
//            return new User(
//                tourGuide.getEmail(),
//                tourGuide.getPassword(),
//                Collections.singletonList(() -> "ROLE_TOURGUIDE") // Gán quyền cho TourGuide
//            );
//        } else {
//        	System.out.println("tourGuide null");
//        }

        throw new UsernameNotFoundException("User not found");
    }
}
