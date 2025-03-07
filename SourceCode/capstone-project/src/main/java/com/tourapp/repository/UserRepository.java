package com.tourapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tourapp.entity.AppUser;



@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
	
    boolean existsByEmail(String email);    
    AppUser findByEmail(String email);
}