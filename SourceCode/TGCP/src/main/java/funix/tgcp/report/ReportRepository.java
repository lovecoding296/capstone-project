package funix.tgcp.report;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByResolved(boolean resolved); // Lấy các báo cáo đã giải quyết hoặc chưa giải quyết
    List<Report> findByReportedById(Long userId); // Lấy các báo cáo của một người dùng
    List<Report> findByReportType(String reportType); // Lấy các báo cáo theo loại
    List<Report> findByReportedUserId(Long userId); // Lấy báo cáo người dùng cụ thể
    List<Report> findByReportedPostId(Long postId); // Lấy báo cáo bài viết cụ thể
    List<Report> findByReportedTourId(Long tourId); // Lấy báo cáo Tour cụ thể
}
