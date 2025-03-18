package funix.tgcp.trip;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import funix.tgcp.user.User;
import funix.tgcp.user.UserRepository;

@Service
public class TripService {
	
	@Autowired
    private TripRepository tripRepository;
	
	@Autowired
    private UserRepository userRepository;
    
    public List<Trip> findAll() {
        return tripRepository.findAll();
    }
    
    public Trip findById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
    }
    
    public Trip createTrip(Trip trip, Long userId) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        trip.setCreator(creator);
        trip.setStatus(TripStatus.PENDING);
        return tripRepository.save(trip);
    }
    
    public Trip updateTrip(Long id, Trip tripDetails) {
        Trip trip = findById(id);
        trip.setTitle(tripDetails.getTitle());
        trip.setCity(tripDetails.getCity());
        trip.setStartDate(tripDetails.getStartDate());
        trip.setEndDate(tripDetails.getEndDate());
        trip.setDescription(tripDetails.getDescription());
        trip.setPrice(tripDetails.getPrice());
        trip.setAgeRestricted(tripDetails.isAgeRestricted());
        trip.setFromAge(tripDetails.getFromAge());
        trip.setToAge(tripDetails.getToAge());
        trip.setCategory(tripDetails.getCategory());
        trip.setMaxParticipants(tripDetails.getMaxParticipants());
        return tripRepository.save(trip);
    }
    
    public void deleteTrip(Long id) {
        Trip trip = findById(id);
        tripRepository.delete(trip);
    }
    
    public List<Trip> findByTripStatus(TripStatus status) {
        return tripRepository.findByStatus(status);
    }
    
    public List<Trip> findByCategory(TripCategory category) {
        return tripRepository.findByCategory(category);
    }
    
    public List<Trip> findByCreatorId(Long userId) {
        return tripRepository.findByCreatorId(userId);
    }
    
    public List<Trip> findByCreatorIdAndTripStatus(Long userId, TripStatus status) {
        return tripRepository.findByCreatorIdAndStatus(userId, status);
    }

	public List<Trip> findByParticipantId(Long userId) {
		return tripRepository.findByParticipantId(userId);
	}

	
}

