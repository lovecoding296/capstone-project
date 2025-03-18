package funix.tgcp.booking;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);  // Truy vấn theo userId
    List<Booking> findByTripId(Long tripId);  // Truy vấn theo tripId
}

