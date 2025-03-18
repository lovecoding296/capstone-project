package funix.tgcp.trip;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByStatus(TripStatus status);
    List<Trip> findByCategory(TripCategory category);
    List<Trip> findByCreatorId(Long userId);
    List<Trip> findByCreatorIdAndStatus(Long userId, TripStatus status);
    
    
    @Query("SELECT t FROM Trip t JOIN t.participants p WHERE p.id = :userId")
	List<Trip> findByParticipantId(Long userId);
}


