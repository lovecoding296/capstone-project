package funix.tgcp.booking.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonIgnore;

import funix.tgcp.booking.Booking;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore
    private Booking booking;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    // Thông tin tài khoản người gửi
    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String senderAccountNumber;

    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String senderAccountName;

    // Nội dung chuyển khoản
    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String transactionNote;

    // Lưu ảnh xác nhận giao dịch (URL)
    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String transactionImageUrl;   

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type = PaymentType.PAYMENT;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

