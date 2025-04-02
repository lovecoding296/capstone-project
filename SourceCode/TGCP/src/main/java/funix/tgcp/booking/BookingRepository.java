package funix.tgcp.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import funix.tgcp.tour.Tour;
import funix.tgcp.user.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);  // Truy vấn theo userId
    List<Booking> findByTourId(Long tourId);  // Truy vấn theo tourId
    
    boolean existsByUserAndTour(User user, Tour tour);
    Booking findByUserIdAndTourId(Long userId, Long tourId);
}

