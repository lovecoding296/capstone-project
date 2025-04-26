package funix.tgcp.report;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportRepository extends JpaRepository<Report, Long> {
    
	List<Report> findByReportTypeAndTargetId(ReportType type, Long targetId);
	
	@Query("SELECT r FROM Report r " +
	       "WHERE (:reporter IS NULL OR r.reporter.fullName LIKE CONCAT('%', :reporter, '%')) " +
	       "AND (:reason IS NULL OR r.reason LIKE CONCAT('%', :reason, '%')) " +
	       "AND (:resolved IS NULL OR r.resolved = :resolved) " +
	       "AND (:reportType IS NULL OR r.reportType = :reportType) " +
	       "ORDER BY r.id ASC")
	Page<Report> findReportByFilter(
	        @Param("reporter") String reporter,
	        @Param("reason") String reason,
	        @Param("resolved") Boolean resolved,
	        @Param("reportType") ReportType reportType,
	        Pageable pageable);

}
