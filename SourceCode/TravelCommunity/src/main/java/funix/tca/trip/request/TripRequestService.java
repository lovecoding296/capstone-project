package funix.tca.trip.request;

import funix.tca.appuser.AppUser;
import funix.tca.trip.Trip;
import funix.tca.trip.TripRepository;
import funix.tca.trip.TripService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TripRequestService {

    @Autowired
    private TripRequestRepository tripRequestRepository;
    
    @Autowired
    private TripService tripService;
    
    @Autowired
    private TripRepository tripRepository;

    public void save(TripRequest request) {
        tripRequestRepository.save(request);
    }

    public Optional<TripRequest> findById(Long id) {
        return tripRequestRepository.findById(id);
    }

    public List<TripRequest> findByTrip(Trip trip) {
        return tripRequestRepository.findByTrip(trip);
    }

    public boolean hasUserRequested(AppUser user, Trip trip) {
        return tripRequestRepository.existsByUserAndTrip(user, trip);
    }

	public boolean approveRequest(Long requestId) {
		Optional<TripRequest> optionalRequest = tripRequestRepository.findById(requestId);
        if (optionalRequest.isPresent()) {
            TripRequest request = optionalRequest.get();
            request.setStatus(RequestStatus.APPROVED);
            tripRequestRepository.save(request);
            
            // Thêm người tham gia vào chuyến đi
            tripService.addParticipantToTrip(request.getUser(), request.getTrip());
            return true;
        }
        return false;
	}

	public boolean rejectRequest(Long requestId) {
		Optional<TripRequest> optionalRequest = tripRequestRepository.findById(requestId);
        if (optionalRequest.isPresent()) {
            TripRequest request = optionalRequest.get();
            request.setStatus(RequestStatus.REJECTED);
            tripRequestRepository.save(request);
            return true;
        }
        return false;
	}

	public TripRequest findRequestByUserAndTrip(AppUser user, Trip trip) {
		return tripRequestRepository.findRequestByUserAndTrip(user, trip);
	}

	public void delete(TripRequest tripRequest) {
		tripRequestRepository.delete(tripRequest);
	}
}
