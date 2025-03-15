package funix.tca.trip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import funix.tca.appuser.Gender;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByCreatorId(Long creatorId); // Lấy các chuyến đi do một người tổ chức
    List<Trip> findByParticipantsId(Long participantId); // Lấy các chuyến đi mà người tham gia tham gia
	Optional<Trip> getTripById(Long tripId);
	List<Trip> findTop3ByCreatorId(Long id);


	
	

    @Query("SELECT t FROM Trip t WHERE " +
           "(:destination IS NULL OR t.destination LIKE %:destination%) AND " +
           "(:category IS NULL OR t.category = :category) AND " +
           "(:gender IS NULL OR t.gender = :gender)")
    List<Trip> findByFilters(@Param("destination") String destination,
                             @Param("category") TripCategory category,
                             @Param("language") String language,
                             @Param("gender") Gender gender);
	

}
