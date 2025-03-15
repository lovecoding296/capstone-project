package funix.tca.trip;

import funix.tca.appuser.AppUser;
import funix.tca.appuser.Gender;
import funix.tca.review.ReviewRepository;
import funix.tca.trip.request.TripRequest;
import funix.tca.trip.request.TripRequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private TripRequestRepository tripRequestRepository;
    
    

    // Lưu một chuyến đi
    public Trip save(Trip trip) {
    	if(trip.getTripPictureUrl() == null || trip.getTripPictureUrl().isBlank()) {
    		trip.setTripPictureUrl("/uploads/default-trip.jpg");
    	}
        return tripRepository.save(trip);
    }

    // Lấy một chuyến đi theo ID
    public Optional<Trip> findById(Long id) {
        return tripRepository.findById(id);
    }

    // Lấy tất cả các chuyến đi
    public List<Trip> findAll() {
        return tripRepository.findAll();
    }

    // Lấy các chuyến đi do một người tổ chức
    public List<Trip> findByCreatorId(Long creatorId) {
        return tripRepository.findByCreatorId(creatorId);
    }

    // Lấy các chuyến đi mà người tham gia tham gia
    public List<Trip> findByParticipantsId(Long participantId) {
        return tripRepository.findByParticipantsId(participantId);
    }

    // Cập nhật chuyến đi
    public Trip update(Trip trip) {
        return tripRepository.save(trip);
    }

    // Xóa chuyến đi theo ID
    @Transactional
    public void deleteById(Long id) {
    	System.out.println("TripService deleteById id = " + id);
    	reviewRepository.deleteByTripId(id);
    	tripRequestRepository.deleteByTripId(id);
        tripRepository.deleteById(id);
    }
    
    public List<Trip> getLatestTrips() {
        return tripRepository.findTop4ByOrderByCreatedAtDesc();
    }

    public void deleteParticipantFromTrip(AppUser user, Trip trip) {
    	
        if (trip.getParticipants().contains(user)) {
        	TripRequest tripRequest = tripRequestRepository.findRequestByUserAndTrip(user, trip);
        	
            // Nếu tìm thấy yêu cầu tham gia, xóa yêu cầu
            if (tripRequest != null) {
            	tripRequestRepository.delete(tripRequest);  // Xóa yêu cầu khỏi cơ sở dữ liệu
            }
        	
            trip.getParticipants().remove(user);            
            System.out.println("deleteParticipantFromTrip thành công");
            tripRepository.save(trip);
        }
    }

    
	public void addParticipantToTrip(AppUser user, Trip trip) {
		trip.getParticipants().add(user);
        tripRepository.save(trip);
		
	}

	public Optional<Trip> getTripById(Long tripId) {
		return tripRepository.getTripById(tripId);
	}

	public List<Trip> findTop3ByCreatorId(Long id) {
		return tripRepository.findTop3ByCreatorId(id);
	}

	public List<Trip> searchTrips(String destination, TripCategory category, String language, Gender gender) {
        return tripRepository.findByFilters(destination, category, language, gender);
    }
}
