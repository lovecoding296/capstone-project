package com.tourapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tourapp.entity.Account;



@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	
    // Kiểm tra xem email đã tồn tại chưa
    boolean existsByEmail(String email);
    
    // Tìm kiếm Tourist theo email
    Account findByEmail(String email);
}