package funix.tgcp.booking;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerId(Long customerId); 
    List<Booking> findByGuideId(Long guideId);
	List<Booking> findCompletedByGuideId(Long guideId);
	
	
	
	
	
	
	
	
    @Query("SELECT b FROM Booking b WHERE b.customer.id = :customerId " +
            "AND (:destination IS NULL OR b.destination LIKE %:destination%) " +
            "AND (:startDate IS NULL OR b.startDate = :startDate) " +
            "AND (:endDate IS NULL OR b.endDate = :endDate) " +
            "AND (:guide IS NULL OR b.guide.fullName LIKE %:guide%) " +
            "AND (:status IS NULL OR b.status = :status)")
     List<Booking> findBookingByCustomerAndFilter(
         @Param("customerId") Long customerId,
         @Param("destination") String destination,
         @Param("startDate") LocalDate startDate,
         @Param("endDate") LocalDate endDate,
         @Param("guide") String guide,
         @Param("status") BookingStatus status
     );


    
    @Query("SELECT b FROM Booking b WHERE b.guide.id = :guideId " +
            "AND (:destination IS NULL OR b.destination LIKE %:destination%) " +
            "AND (:startDate IS NULL OR b.startDate = :startDate) " +
            "AND (:endDate IS NULL OR b.endDate = :endDate) " +
            "AND (:user IS NULL OR b.customer.fullName LIKE %:user%) " +
            "AND (:status IS NULL OR b.status = :status)")
     List<Booking> findBookingByGuideAndFilter(
         @Param("guideId") Long guideId,
         @Param("destination") String destination,
         @Param("startDate") LocalDate startDate,
         @Param("endDate") LocalDate endDate,
         @Param("user") String user,
         @Param("status") BookingStatus status
     );
    
}

