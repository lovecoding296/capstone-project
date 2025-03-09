package com.tourapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tourapp.entity.AppUser;



@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
	
    boolean existsByEmail(String email);    
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findUserById(Long id);
}