package com.tourapp.entity;

public enum BookingStatus {
    PENDING,          // Đặt chỗ đang chờ phê duyệt
    CONFIRMED,        // Đặt chỗ đã xác nhận
    COMPLETED,        // Tour đã hoàn thành
    CANCELLED,        // Đặt chỗ đã bị hủy
    PAYMENT_PENDING,  // Chờ thanh toán
    PAYMENT_CONFIRMED // Thanh toán đã xác nhận
}

