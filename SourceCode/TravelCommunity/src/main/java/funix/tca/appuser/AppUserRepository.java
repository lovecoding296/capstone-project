package funix.tca.appuser;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
	Optional<AppUser> findByEmail(String email); // Tìm kiếm theo email

	List<AppUser> findByKycApprovedFalse();
}

