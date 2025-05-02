package funix.tgcp.guide.service;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import funix.tgcp.booking.payment.PaymentOption;
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
	
	
	@Query("SELECT gs FROM GuideService gs " +
	       "JOIN gs.guide u " + // JOIN với bảng User
	       "WHERE (:serviceType IS NULL OR gs.type = :serviceType) " +
	       "AND (:city IS NULL OR gs.city = :city) " +
	       "AND (:language IS NULL OR gs.language = :language) " +
	       "AND (:paymentOption IS NULL OR gs.paymentOption = :paymentOption) " +
	       "AND (:groupSize IS NULL OR gs.groupSizeCategory = :groupSize) " +
	       "AND (:maxPrice IS NULL OR gs.price <= :maxPrice) " +
	       "AND (:minRating IS NULL OR u.averageRating >= :minRating) " +
	       "AND (:guideName IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :guideName, '%')))")
	Page<GuideService> searchServices(ServiceType serviceType, City city, Language language,
	                                  GroupSizeCategory groupSize, PaymentOption paymentOption, Double maxPrice, Integer minRating,
	                                  String guideName, Pageable pageable);
	
	
	@Query("SELECT gs FROM GuideService gs " +
	       "WHERE gs.guide.id = :id " +
	       "AND (:serviceType IS NULL OR gs.type = :serviceType) " +
	       "AND (:city IS NULL OR gs.city = :city) " +
	       "AND (:language IS NULL OR gs.language = :language) " +
	       "AND (:paymentOption IS NULL OR gs.paymentOption = :paymentOption) " +
	       "AND (:groupSize IS NULL OR gs.groupSizeCategory = :groupSize)")
	Page<GuideService> findByGuideIdAndFilter(@Param("id") Long id,
	                                          @Param("serviceType") ServiceType serviceType,
	                                          @Param("city") City city,
	                                          @Param("language") Language language,
	                                          @Param("groupSize") GroupSizeCategory groupSize,
	                                          @Param("paymentOption") PaymentOption paymentOption,
	                                          Pageable pageable);

}