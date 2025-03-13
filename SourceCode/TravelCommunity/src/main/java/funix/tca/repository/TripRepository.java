package funix.tca.repository;

import funix.tca.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByCreatorId(Long creatorId); // Lấy các chuyến đi do một người tổ chức
    List<Trip> findByParticipantsId(Long participantId); // Lấy các chuyến đi mà người tham gia tham gia
	Optional<Trip> getTripById(Long tripId);
}
