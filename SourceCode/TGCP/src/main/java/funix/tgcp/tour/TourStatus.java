package funix.tgcp.tour;

public enum TourStatus {
    PENDING,               // Chờ phê duyệt hoặc chỉnh sửa trước khi công khai
    OPEN,                  // Đã công khai, người dùng có thể tham gia
    REGISTRATION_CLOSED,   // Đã đóng đăng ký, nhưng chưa diễn ra
    ONGOING,               // Chuyến đi đang diễn ra
    COMPLETED,             // Chuyến đi đã kết thúc
    CANCELED               // Chuyến đi bị hủy
}

