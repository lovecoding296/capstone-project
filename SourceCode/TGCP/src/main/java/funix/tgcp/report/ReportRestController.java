package funix.tgcp.report;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.user.Role;
import funix.tgcp.util.LogHelper;

@RestController
@RequestMapping("/api/reports")
public class ReportRestController {

	private static final LogHelper logger = new LogHelper(ReportRestController.class);

	
	@Autowired
    private ReportService reportService;


    @GetMapping()
    public Page<Report> findReportByFilter(
    		@RequestParam(required = false) String reporter,
	        @RequestParam(required = false) String reason,
	        @RequestParam(required = false) Boolean resolved, 
	        @RequestParam(required = false) ReportType reportType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
	        ) {    	
    	
    	Pageable pageable = PageRequest.of(page, size);
    	return reportService.findReportByFilter(
    			reporter,
    			reason,
    			resolved,
    			reportType,
    			pageable
    			);
    }

    @PostMapping("/create")
    public ResponseEntity<String> report(
    		@RequestBody Report reportRequest,
    		@AuthenticationPrincipal CustomUserDetails userDetails) {    	
    	
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
