package funix.tgcp.guide.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import funix.tgcp.user.City;
import funix.tgcp.user.Language;

@Service
public class GuideServiceService {

    @Autowired
    private GuideServiceRepository guideServiceRepo;

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
        guideService.setPrice(guideServiceDetails.getPrice());
        guideService.setCity(guideServiceDetails.getCity());
                
        return guideServiceRepo.save(guideService);
    }
    
    public Optional<GuideService> getGuideServiceById(Long id) {
        return guideServiceRepo.findById(id);
    }

    public void deleteGuideService(Long id) {
    	guideServiceRepo.deleteById(id);
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
}