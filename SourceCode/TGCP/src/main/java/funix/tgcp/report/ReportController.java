package funix.tgcp.report;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.util.LogHelper;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

	private static final LogHelper logger = new LogHelper(ReportController.class);

	
	@Autowired
    private ReportService reportService;


    @GetMapping()
    public ResponseEntity<List<Report>> getAll() {    	
    	
    	return ResponseEntity.ok(reportService.getAll());
    }

    @PostMapping("/create")
    public ResponseEntity<String> report(@RequestBody Report reportRequest) {
    	
    	
    	CustomUserDetails userDetails = CustomUserDetails.getCurrentUserDetails();
    	logger.info("userDetails " + userDetails);

    	reportRequest.setReporter(userDetails.getUser());  
    	
        reportService.createReport(reportRequest);
        return ResponseEntity.ok("Report submitted successfully.");
    }
    
    
    @PutMapping("/{id}")
    public ResponseEntity<String> resolve(@RequestBody Report reportRequest) {
          return ResponseEntity.ok(reportService.resolve(reportRequest));
    }
}
