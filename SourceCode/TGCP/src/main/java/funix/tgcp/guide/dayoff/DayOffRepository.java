package funix.tgcp.guide.dayoff;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DayOffRepository extends JpaRepository<DayOff, Long> {

    List<DayOff> findByGuideId(Long guideId);

    boolean existsByGuideIdAndDate(Long guideId, LocalDate date);
    
    void deleteByBookingId(Long bookingId);
}

