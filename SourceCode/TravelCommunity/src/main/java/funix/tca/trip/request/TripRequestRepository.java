package funix.tca.trip.request;

import funix.tca.appuser.AppUser;
import funix.tca.trip.Trip;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRequestRepository extends JpaRepository<TripRequest, Long> {
    List<TripRequest> findByTrip(Trip trip);
    boolean existsByUserAndTrip(AppUser user, Trip trip);
    TripRequest findRequestByUserAndTrip(AppUser user, Trip trip);
	void deleteByTripId(Long id);
}
