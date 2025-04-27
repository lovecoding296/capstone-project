package funix.tgcp.guide.service;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import funix.tgcp.user.City;
import funix.tgcp.user.Language;

@Repository
public interface GuideServiceRepository extends JpaRepository<GuideService, Long> {

	boolean existsByGuideIdAndTypeAndGroupSizeCategoryAndLanguageAndCity(
            Long guideId, 
            ServiceType type, 
            GroupSizeCategory groupSizeCategory, 
            Language language, 
            City city);
	
	boolean existsByGuideIdAndTypeAndGroupSizeCategoryAndLanguageAndCityAndIdNot(
		    Long guideId, 
		    ServiceType serviceType, 
		    GroupSizeCategory groupSizeCategory, 
		    Language language, 
		    City city, 
		    Long id);	
	
	Page<GuideService> findByGuideId(Long userId, Pageable pageable);

	List<GuideService> findByGuideId(Long userId);
	
	
	
	Optional<GuideService> findByGuideIdAndTypeAndGroupSizeCategoryAndLanguageAndCity(
			Long guideId, 
            ServiceType type, 
            GroupSizeCategory groupSizeCategory, 
            Language language, 
            City city);
}