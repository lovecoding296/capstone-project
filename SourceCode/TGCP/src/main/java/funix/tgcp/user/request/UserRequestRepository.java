package funix.tgcp.user.request;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRequestRepository extends JpaRepository<UserRequest, Long> {

	Optional<UserRequest> findByVerificationToken(String token);

}
