package funix.tgcp.tour.image;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TourImageRepository extends JpaRepository<TourImage, Long> {
    TourImage findByUrl(String url);
}

