package funix.tgcp.booking.payment;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    @NotNull
    @Positive(message = "Amount must be greater than 0")
    private double amount;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentStatus status = PaymentStatus.PENDING;

    // Thông tin tài khoản người gửi
    @Column(columnDefinition = "NVARCHAR(255)")
    @NotBlank(message = "senderAccountNumber URL is required")
    @Size(max = 255)
    private String senderAccountNumber;

    @Column(columnDefinition = "NVARCHAR(255)")
    @NotBlank(message = "Sender account name is required")
    @Size(max = 255)
    private String senderAccountName;

    // Nội dung chuyển khoản
    @Column(columnDefinition = "NVARCHAR(255)")
    @NotBlank(message = "Transaction note is required")
    @Size(max = 255)
    private String transactionNote;

    // Lưu ảnh xác nhận giao dịch (URL)
    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    @Size(max = 255)
    private String transactionImageUrl;   

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentType type = PaymentType.PAYMENT;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();
}