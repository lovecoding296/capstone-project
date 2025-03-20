package funix.tgcp.trip.itinerary;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ItineraryService {

    private final ItineraryRepository itineraryRepository;
    private final ActivityRepository activityRepository;

    public ItineraryService(ItineraryRepository itineraryRepository, ActivityRepository activityRepository) {
        this.itineraryRepository = itineraryRepository;
		this.activityRepository = activityRepository;
    }

    public List<Itinerary> findAll() {
        return itineraryRepository.findAll();
    }

    public Itinerary findItineraryById(Long id) {
        return itineraryRepository.findById(id).orElse(null);
    }
    
    public Activity findActivityById(Long id) {
        return activityRepository.findById(id).orElse(null);
    }

    public Itinerary createItinerary(Itinerary itinerary) {
        return itineraryRepository.save(itinerary);
    }

    public Itinerary updateItinerary(Itinerary itinerary) {
        return itineraryRepository.save(itinerary);
    }
    public void deleteItineraryById(Long id) {
        itineraryRepository.deleteById(id);
    }
    
    public void deleteActivityById(Long id) {
    	activityRepository.deleteById(id);
    }

	public Activity createActivity(Activity activity) {
		return activityRepository.save(activity);		
	}
	
	public void updateActivity(Activity activity) {
		activityRepository.save(activity);		
	}
    
    
}

