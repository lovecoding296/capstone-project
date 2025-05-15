package funix.tgcp.report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import funix.tgcp.notification.NotificationService;

@Service
public class ReportService {

	@Autowired
    private ReportRepository reportRepo;
	
	@Autowired NotificationService notiService;
    
	public List<Report> getAll() {
		return reportRepo.findAll();
	}

	public void createReport(Report reportRequest) {
		      
        notiService.sendNotificationToAdmin(
        		reportRequest.getReporter().getFullName() + " reported a " + reportRequest.getReportType() + ", plz check it!",
        		"/admin/dashboard#manage-reports");		
        
		reportRepo.save(reportRequest);		
	}
	
	
	public String resolve(Report reportRequest) {
	    Optional<Report> optionalReport = reportRepo.findById(reportRequest.getId());

	    if (optionalReport.isPresent()) {
	        Report report = optionalReport.get();
	        report.setAdminFeedBack(reportRequest.getAdminFeedBack());
	        report.setResolved(true);
	        report.setResolvedTime(LocalDateTime.now());
	        reportRepo.save(report);
	        
	        String baseLink = report.getReportType() == ReportType.POST ? "/posts/" : "/users/";
	        String sourceLink = baseLink + report.getTargetId();
	        notiService.sendNotification(
	        		report.getReporter(),
	        		"Admin resolved your report! (" + report.getAdminFeedBack() + ")",
	        		sourceLink);
	        
	        return "Resolved report successfully!";
	    } else {
	    	return "Failed to resolve report, try again later!";
	    }
	}


	public Page<Report> findReportByFilter(String reporter, String reason, Boolean resolved, ReportType reportType,
			Pageable pageable) {
		return reportRepo.findReportByFilter(reporter, reason, resolved, reportType, pageable);
	}
}
