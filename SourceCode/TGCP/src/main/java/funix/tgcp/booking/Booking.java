package funix.tgcp.booking;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.validation.annotation.Validated;

import funix.tgcp.booking.payment.Payment;
import funix.tgcp.guide.service.GuideService;
import funix.tgcp.user.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "guide_id", nullable = false)
    private User guide;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;    
    
    @ManyToOne
    @JoinColumn(name = "guide_service_id")
    private GuideService guideService;
    
    private double totalPrice = 0;
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String destination;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String reason;
    
    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;    
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Payment> payments;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime canceledAt;
}

