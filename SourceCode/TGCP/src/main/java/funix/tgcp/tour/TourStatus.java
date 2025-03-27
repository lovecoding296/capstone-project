package funix.tgcp.tour;

public enum TourStatus {
    PENDING,               // Chờ phê duyệt hoặc chỉnh sửa trước khi công khai
    REJECTED,			  
    OPEN,                  // Đã công khai, người dùng có thể tham gia
    REGISTRATION_CLOSED,   // Đã đóng đăng ký, nhưng chưa diễn ra
    ONGOING,               // Tour đang diễn ra
    COMPLETED,             // Tour đã kết thúc
    CANCELED               // Tour bị hủy
}

