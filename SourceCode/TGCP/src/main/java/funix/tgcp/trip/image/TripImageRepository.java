package funix.tgcp.trip.image;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TripImageRepository extends JpaRepository<TripImage, Long> {
    TripImage findByUrl(String url);
}

