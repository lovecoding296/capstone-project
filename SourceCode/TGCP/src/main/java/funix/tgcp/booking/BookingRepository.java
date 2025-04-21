package funix.tgcp.booking;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerId(Long customerId); 
    List<Booking> findByGuideId(Long guideId);
    
}

