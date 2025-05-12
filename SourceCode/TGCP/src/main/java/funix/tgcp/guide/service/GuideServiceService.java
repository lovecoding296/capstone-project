package funix.tgcp.guide.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import funix.tgcp.booking.Booking;
import funix.tgcp.booking.BookingRepository;
import funix.tgcp.booking.payment.PaymentOption;
import funix.tgcp.user.City;
import funix.tgcp.user.Language;
import funix.tgcp.user.User;
import funix.tgcp.user.UserRepository;

@Service
public class GuideServiceService {
	
	private static final Logger logger = LoggerFactory.getLogger(GuideServiceRestController.class);


    @Autowired
    private GuideServiceRepository guideServiceRepo;
    
    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
	private UserRepository userRepo;

    public GuideService createGuideService(GuideService guideService) {
    	
    	if (guideServiceRepo.existsByGuideIdAndTypeAndGroupSizeCategoryAndLanguageAndCity(guideService.getGuide().getId(), 
    			guideService.getType(),
    			guideService.getGroupSizeCategory(), 
    			guideService.getLanguage(), 
    			guideService.getCity())) {
            throw new RuntimeException("Service already exists with the same parameters.");
        }
    	
        return guideServiceRepo.save(guideService);
    }

  

    public GuideService updateGuideService(Long id, GuideService guideServiceDetails) {
        GuideService guideService = guideServiceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("GuideService not found"));
        
        
        if (guideServiceRepo.existsByGuideIdAndTypeAndGroupSizeCategoryAndLanguageAndCityAndIdNot(
                guideService.getGuide().getId(), 
                guideServiceDetails.getType(),
                guideServiceDetails.getGroupSizeCategory(), 
                guideServiceDetails.getLanguage(),
                guideServiceDetails.getCity(),
                id)) {
            throw new RuntimeException("Service already exists with the same parameters.");
        }

        guideService.setType(guideServiceDetails.getType());
        guideService.setGroupSizeCategory(guideServiceDetails.getGroupSizeCategory());
        guideService.setLanguage(guideServiceDetails.getLanguage());
        guideService.setPricePerDay(guideServiceDetails.getPricePerDay());
        guideService.setCity(guideServiceDetails.getCity());
                
        return guideServiceRepo.save(guideService);
    }
    
    public Optional<GuideService> getGuideServiceById(Long id) {
        return guideServiceRepo.findById(id);
    }

    public boolean deleteGuideService(Long guideId, Long serviceId) {  	
    	if(!bookingRepo.existsByGuideServiceId(serviceId)) {
    		User guide = userRepo.findById(guideId).orElseThrow();
    		GuideService serviceToRemove = guide.getGuideServices()
    		    .stream()
    		    .filter(gs -> gs.getId().equals(serviceId))
    		    .findFirst()
    		    .orElseThrow();

    		guide.getGuideServices().remove(serviceToRemove);
    		userRepo.save(guide);    		
    		return true;
    	}    	
    	return false;
    }

	public Page<GuideService> findByGuideId(Long userId, Pageable pageable) {		
		return guideServiceRepo.findByGuideId(userId, pageable);
	}
	
	public List<GuideService> findByGuideId(Long userId) {		
		return guideServiceRepo.findByGuideId(userId);
	}
	
	
	public Optional<GuideService> findByGuideIdAndTypeAndGroupSizeCategoryAndLanguageAndCity(Long guideId, 
            ServiceType type, 
            GroupSizeCategory groupSizeCategory, 
            Language language, 
            City city) {
		return guideServiceRepo.findByGuideIdAndTypeAndGroupSizeCategoryAndLanguageAndCity(guideId, type, groupSizeCategory, language, city);
	}
	
	public Optional<GuideService> findValidGuideService(Long guideId, GuideService guideService) {
		return guideServiceRepo.findByGuideIdAndTypeAndGroupSizeCategoryAndLanguageAndCity(guideId, 
				guideService.getType(), 
				guideService.getGroupSizeCategory(), 
				guideService.getLanguage(), 
				guideService.getCity());
	}



	public Page<GuideService> searchServices(ServiceType serviceType, City city, Language language,
			GroupSizeCategory groupSize, PaymentOption paymentOption, Double maxPrice, Integer minRating, String guideName, Pageable pageable) {
		return guideServiceRepo.searchServices(serviceType, city, language, groupSize, paymentOption, maxPrice, minRating, guideName, pageable);
	}



	public Page<GuideService> findByGuideIdAndFilter(Long id, ServiceType serviceType, City city, Language language,
			GroupSizeCategory groupSize, PaymentOption paymentOption, Pageable pageable) {
		return guideServiceRepo.findByGuideIdAndFilter(id, serviceType, city, language, groupSize, paymentOption, pageable);
	}
	
	
	
}