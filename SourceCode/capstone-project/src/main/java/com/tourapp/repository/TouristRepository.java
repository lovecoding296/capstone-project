package com.tourapp.repository;

import com.tourapp.entity.Tourist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TouristRepository extends JpaRepository<Tourist, Long> {
    
    // Kiểm tra xem email đã tồn tại chưa
    boolean existsByEmail(String email);
    
    // Tìm kiếm Tourist theo email
    Tourist findByEmail(String email);
}
