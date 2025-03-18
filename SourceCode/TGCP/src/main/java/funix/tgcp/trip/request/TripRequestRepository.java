package funix.tgcp.trip.request;

import org.springframework.data.jpa.repository.JpaRepository;

import funix.tgcp.appuser.AppUser;
import funix.tgcp.trip.Trip;

import java.util.List;

public interface TripRequestRepository extends JpaRepository<TripRequest, Long> {
    List<TripRequest> findByTrip(Trip trip);
    boolean existsByUserAndTrip(AppUser user, Trip trip);
    TripRequest findRequestByUserAndTrip(AppUser user, Trip trip);
	void deleteByTripId(Long id);
}
