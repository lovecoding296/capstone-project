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
	
	
	@Query("SELECT u FROM User u WHERE (:email IS NULL OR u.email LIKE %:email%) "
	       + "AND (:fullName IS NULL OR u.fullName LIKE %:fullName%) "
	       + "AND (:role IS NULL OR u.role = :role) "
	       + "AND (:kycApproved IS NULL OR u.kycApproved = :kycApproved) "
	       + "AND (:enabled IS NULL OR u.enabled = :enabled) "
	       + "AND (:verified IS NULL OR u.verified = :verified) "
	       + "ORDER BY u.id ASC")
	List<User> findUserByFilter(
	        @Param("email") String email,
	        @Param("fullName") String fullName,
	        @Param("role") Role role,
	        @Param("kycApproved") Boolean kycApproved,
	        @Param("enabled") Boolean enabled,
	        @Param("verified") Boolean verified);


}

