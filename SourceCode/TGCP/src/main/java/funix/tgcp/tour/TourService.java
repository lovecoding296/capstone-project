package funix.tgcp.tour;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import funix.tgcp.user.User;
import funix.tgcp.user.UserRepository;
import funix.tgcp.util.LogHelper;

@Service
public class TourService {
	
	private static final LogHelper logger = new LogHelper(TourService.class);
	
	@Autowired
    private TourRepository tourRepository;
	
	@Autowired
    private UserRepository userRepository;
    
    public List<Tour> findAll() {
        return tourRepository.findAll();
    }
    
    public Tour findById(Long id) {
        return tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
    }
    
    public Tour createTour(Tour tour, Long userId) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        tour.setCreator(creator);
        tour.setStatus(TourStatus.PENDING);
        return tourRepository.save(tour);
    }
    
    public Tour updateTour(Long id, Tour tourDetails) {
        Tour tour = findById(id);
        tour.setTitle(tourDetails.getTitle());
        tour.setCity(tourDetails.getCity());
        tour.setStartDate(tourDetails.getStartDate());
        tour.setEndDate(tourDetails.getEndDate());
        tour.setDescription(tourDetails.getDescription());
        tour.setPrice(tourDetails.getPrice());
        tour.setAgeRestricted(tourDetails.isAgeRestricted());
        tour.setFromAge(tourDetails.getFromAge());
        tour.setToAge(tourDetails.getToAge());
        tour.setCategory(tourDetails.getCategory());
        tour.setMaxParticipants(tourDetails.getMaxParticipants());
        tour.setStatus(TourStatus.PENDING);
        return tourRepository.save(tour);
    }
    
    public void deleteTour(Long id) {
        Tour tour = findById(id);
        tourRepository.delete(tour);
    }
    
    public List<Tour> findByTourStatus(TourStatus status) {
        return tourRepository.findByStatus(status);
    }
    
    public List<Tour> findByCategory(TourCategory category) {
        return tourRepository.findByCategory(category);
    }
    
    public List<Tour> findByCreatorId(Long userId) {
        return tourRepository.findByCreatorId(userId);
    }
    
    public List<Tour> findByCreatorIdAndTourStatus(Long userId, TourStatus status) {
        return tourRepository.findByCreatorIdAndStatus(userId, status);
    }

	public List<Tour> findByParticipantId(Long userId) {
		return tourRepository.findByParticipantId(userId);
	}

	public List<Tour> getPendingTours() {
		return tourRepository.findByStatus(TourStatus.PENDING);
	}

	public Tour approveTour(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        logger.info("" + tour.getId() + " " + tour.getStatus());
        tour.setStatus(TourStatus.OPEN);
        return tourRepository.save(tour);
    }

    public Tour rejectTour(Long id, String reason) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        logger.info("" + tour.getId() + " " + tour.getStatus());
        tour.setStatus(TourStatus.REJECTED);
        tour.setRejectedReason(reason);
        return tourRepository.save(tour);
    }

	
}

