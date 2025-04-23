package funix.tgcp.busydate;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BusyDateRepository extends JpaRepository<BusyDate, Long> {

    List<BusyDate> findByGuideId(Long guideId);

    boolean existsByGuideIdAndDate(Long guideId, LocalDate date);
    
    void deleteByBookingId(Long bookingId);
}

