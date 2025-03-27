package funix.tgcp.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import funix.tgcp.tour.Tour;
import funix.tgcp.tour.TourService;


@RestController
@RequestMapping("/api/admin/tours")
public class AdminTourController {
	
	@Autowired
	private TourService tourService;
	
	@GetMapping("/pending")
	public List<Tour> getPendingTours() {
		return tourService.getPendingTours();
	}
	
	@PutMapping("/{id}/approve")
    public ResponseEntity<String> approveTour(@PathVariable Long id) {
		tourService.approveTour(id);
        return ResponseEntity.ok("Tour approved successfully.");
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<String> rejectTour(@PathVariable Long id) {
    	tourService.rejectTour(id);
        return ResponseEntity.ok("Tour rejected successfully.");
    }
}
