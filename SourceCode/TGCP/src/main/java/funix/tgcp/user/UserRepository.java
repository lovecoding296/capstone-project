package funix.tgcp.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email); // Tìm kiếm theo email

	List<User> findByKycApprovedFalse();

	Optional<User> findByVerificationToken(String token);

	boolean existsByEmail(String email);
}

