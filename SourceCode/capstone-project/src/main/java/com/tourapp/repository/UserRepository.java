package com.tourapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tourapp.entity.AppUser;
import com.tourapp.entity.Role;



@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
	
    boolean existsByEmail(String email);    
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findUserById(Long id);
    List<AppUser> findByRole(Role roleTourGuide);
}