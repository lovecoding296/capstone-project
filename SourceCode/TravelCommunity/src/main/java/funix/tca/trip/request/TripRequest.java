package funix.tca.trip.request;

import java.time.LocalDateTime;

import funix.tca.appuser.AppUser;
import funix.tca.trip.Trip;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    private LocalDateTime requestDate = LocalDateTime.now(); // Ngày gửi yêu cầu

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING; // Trạng thái yêu cầu (MẶC ĐỊNH: Đang chờ duyệt)

   
}
