package funix.tca.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.validation.annotation.Validated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
public class TripExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String description;
    
    private double amount;
    private LocalDateTime expenseDate;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "paid_by", nullable = false)
    private AppUser paidBy; // Người đã thanh toán

    @ManyToMany
    @JoinTable(
        name = "expense_participants",
        joinColumns = @JoinColumn(name = "expense_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<AppUser> participants; // Những người tham gia chia sẻ chi phí

}
