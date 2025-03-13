package funix.tca.repository;

import funix.tca.entity.AppUser;
import funix.tca.entity.Trip;
import funix.tca.entity.TripRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRequestRepository extends JpaRepository<TripRequest, Long> {
    List<TripRequest> findByTrip(Trip trip);
    boolean existsByUserAndTrip(AppUser user, Trip trip);
    TripRequest findRequestByUserAndTrip(AppUser user, Trip trip);
}
