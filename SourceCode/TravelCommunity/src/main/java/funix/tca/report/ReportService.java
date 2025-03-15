package funix.tca.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    // Lưu báo cáo
    public Report save(Report report) {
        return reportRepository.save(report);
    }

    // Lấy báo cáo theo ID
    public Optional<Report> findById(Long id) {
        return reportRepository.findById(id);
    }

    // Lấy tất cả báo cáo
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // Lấy báo cáo theo tình trạng đã giải quyết hay chưa
    public List<Report> findByResolved(boolean resolved) {
        return reportRepository.findByResolved(resolved);
    }

    // Cập nhật báo cáo
    public Report update(Report report) {
        return reportRepository.save(report);
    }

    // Xóa báo cáo theo ID
    public void deleteById(Long id) {
        reportRepository.deleteById(id);
    }

    // Lấy báo cáo theo loại
    public List<Report> findByReportType(String reportType) {
        return reportRepository.findByReportType(reportType);
    }

    // Lấy báo cáo theo người dùng đã bị báo cáo
    public List<Report> findByReportedUserId(Long userId) {
        return reportRepository.findByReportedUserId(userId);
    }

    // Lấy báo cáo theo bài viết bị báo cáo
    public List<Report> findByReportedPostId(Long postId) {
        return reportRepository.findByReportedPostId(postId);
    }

    // Lấy báo cáo theo chuyến đi bị báo cáo
    public List<Report> findByReportedTripId(Long tripId) {
        return reportRepository.findByReportedTripId(tripId);
    }
}
