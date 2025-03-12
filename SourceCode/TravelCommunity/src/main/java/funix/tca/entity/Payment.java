package funix.tca.entity;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.validation.annotation.Validated;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip; // Chuyến đi liên quan

    @ManyToOne
    @JoinColumn(name = "payer_id", nullable = false)
    private AppUser payer; // Người thanh toán
    
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private AppUser receiver; // Người nhận       

    private double amount;
    private boolean confirmed;
    private LocalDateTime paymentDate;
}

