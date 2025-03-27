package funix.tgcp.guide.request;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import funix.tgcp.user.User;

@Repository
public interface GuideRequestRepository extends JpaRepository<GuideRequest, Long> {
    boolean existsByUserAndStatus(User user, GuideRequestStatus status);
    List<GuideRequest> findByStatus(GuideRequestStatus status);
}

