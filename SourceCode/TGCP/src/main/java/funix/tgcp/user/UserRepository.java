package funix.tgcp.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email); // Tìm kiếm theo email
	
	
	Optional<User> findById(Long id);

	List<User> findByKycApprovedFalse();
	List<User> findByRole(Role role);
	
	List<User> findTop6ByRoleAndKycApprovedTrueAndVerifiedTrueAndIsActiveTrueOrderByAverageRatingDesc(Role role);

	Optional<User> findByVerificationToken(String token);

	boolean existsByEmail(String email);


	
	    
	@Query("SELECT u FROM User u WHERE u.role = 'ROLE_GUIDE' " +
	       "AND (:city IS NULL OR u.city = :city) " +
	       "AND (:maxPrice IS NULL OR u.pricePerDay <= :maxPrice) " +
	       "AND (:gender IS NULL OR u.gender = :gender) " +
	       "AND (:language IS NULL OR :language MEMBER OF u.languages) " +
	       "AND (:isLocalGuide IS NULL OR u.isLocalGuide = :isLocalGuide) " +
	       "AND (:isInternationalGuide IS NULL OR u.isInternationalGuide = :isInternationalGuide)")
	Page<User> findGuideByFilter(@Param("city") City city,
	                             @Param("maxPrice") Integer maxPrice,
	                             @Param("gender") Gender gender,
	                             @Param("language") Language language,
	                             @Param("isLocalGuide") Boolean isLocalGuide,
	                             @Param("isInternationalGuide") Boolean isInternationalGuide,
	                             Pageable pageable);


	
}

