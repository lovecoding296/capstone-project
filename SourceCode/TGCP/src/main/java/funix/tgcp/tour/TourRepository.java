package funix.tgcp.tour;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TourRepository extends JpaRepository<Tour, Long> {
    List<Tour> findByStatus(TourStatus status);
    List<Tour> findByCategory(TourCategory category);
    List<Tour> findByCreatorId(Long userId);
    List<Tour> findByCreatorIdAndStatus(Long userId, TourStatus status);
        
    @Query("SELECT t FROM Tour t JOIN t.participants p WHERE p.id = :userId")
	List<Tour> findByParticipantId(Long userId);
}


