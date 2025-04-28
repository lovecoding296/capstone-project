package funix.tgcp.booking;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerId(Long customerId); 
    List<Booking> findByGuideId(Long guideId);
    
    
    
    @Query("SELECT b FROM Booking b WHERE b.status = 'COMPLETED' "
	     + "AND b.guide.id = :guideId ORDER BY b.endDate")
	List<Booking> findCompletedByGuideId(@Param("guideId") Long guideId);

	
	
	
	@Query("SELECT COUNT(b) FROM Booking b WHERE b.status = 'COMPLETED' "
			+ "AND (b.customer.id = :userId OR b.guide.id = :userId)")
	long countCompletedByUserIdOrGuideId(@Param("userId") Long userId);

	
	
	@Query("SELECT b FROM Booking b WHERE b.customer.id = :customerId " +
	       "AND (:destination IS NULL OR b.destination LIKE CONCAT('%', :destination, '%')) " +
	       "AND (:startDate IS NULL OR b.startDate = :startDate) " +
	       "AND (:endDate IS NULL OR b.endDate = :endDate) " +
	       "AND (:guide IS NULL OR b.guide.fullName LIKE CONCAT('%', :guide, '%')) " +
	       "AND (:status IS NULL OR b.status = :status)")
	Page<Booking> findBookingByCustomerAndFilter(
	    @Param("customerId") Long customerId,
	    @Param("destination") String destination,
	    @Param("startDate") LocalDate startDate,
	    @Param("endDate") LocalDate endDate,
	    @Param("guide") String guide,
	    @Param("status") BookingStatus status,
	    Pageable pageable
	);



    
	@Query("SELECT b FROM Booking b WHERE b.guide.id = :guideId " +
	       "AND (:destination IS NULL OR b.destination LIKE CONCAT('%', :destination, '%')) " +
	       "AND (:startDate IS NULL OR b.startDate = :startDate) " +
	       "AND (:endDate IS NULL OR b.endDate = :endDate) " +
	       "AND (:user IS NULL OR b.customer.fullName LIKE CONCAT('%', :user, '%')) " +
	       "AND (:status IS NULL OR b.status = :status)")
	Page<Booking> findBookingByGuideAndFilter(
	    @Param("guideId") Long guideId,
	    @Param("destination") String destination,
	    @Param("startDate") LocalDate startDate,
	    @Param("endDate") LocalDate endDate,
	    @Param("user") String user,
	    @Param("status") BookingStatus status,
	    Pageable pageable
	);

}

