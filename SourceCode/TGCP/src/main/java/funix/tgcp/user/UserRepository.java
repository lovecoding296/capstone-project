package funix.tgcp.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import funix.tgcp.guide.service.GroupSizeCategory;
import funix.tgcp.guide.service.ServiceType;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email); // Tìm kiếm theo email
	
	
	Optional<User> findById(Long id);
	List<User> findByKycApprovedFalse();
	List<User> findByRole(Role role);
	
	List<User> findTop6ByRoleAndKycApprovedTrueAndAndIsActiveTrueOrderByAverageRatingDesc(Role role);

	Optional<User> findByVerificationToken(String token);

	boolean existsByEmail(String email);
	
	@Query("SELECT DISTINCT u FROM User u JOIN u.guideServices gs WHERE u.role = 'ROLE_GUIDE' AND gs IS NOT NULL")
	List<User> findTopUsersWithGuideServicesAndRoleGuide(Pageable pageable);

	
	    
	@Query("SELECT DISTINCT u FROM User u " +
	       "JOIN u.guideServices gs " +
	       "WHERE (:type IS NULL OR gs.type = :type) " +
	       "AND (:city IS NULL OR gs.city = :city) " +
	       "AND (:language IS NULL OR gs.language = :language) " +
	       "AND (:groupSize IS NULL OR gs.groupSizeCategory = :groupSize) " +
	       "AND (:gender IS NULL OR u.gender = :gender) " +
	       "AND (:isLocalGuide IS NULL OR u.isLocalGuide = :isLocalGuide) " +
	       "AND (:isInternationalGuide IS NULL OR u.isInternationalGuide = :isInternationalGuide) " + 
	       "ORDER BY u.id desc")
	Page<User> findGuideByFilter(ServiceType type, City city, Language language, 
	                             GroupSizeCategory groupSize, Gender gender, 
	                             Boolean isLocalGuide, Boolean isInternationalGuide, Pageable pageable);

	
	
	@Query("SELECT DISTINCT u FROM User u WHERE (:email IS NULL OR u.email LIKE CONCAT('%', :email, '%')) " + 
			"AND (:fullName IS NULL OR u.fullName LIKE CONCAT('%', :fullName, '%')) " + 
			"AND (:role IS NULL OR u.role = :role) " + 
			"AND (:kycApproved IS NULL OR u.kycApproved = :kycApproved) " + 
			"AND (:enabled IS NULL OR u.enabled = :enabled) " + 
			"ORDER BY u.id desc")
	Page<User> findUserByFilter(
	        @Param("email") String email,
	        @Param("fullName") String fullName,
	        @Param("role") Role role,
	        @Param("kycApproved") Boolean kycApproved,
	        @Param("enabled") Boolean enabled,
	        Pageable pageable);





}

