package funix.tgcp.report;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByReportTypeAndTargetId(ReportType type, Long targetId);
}
