package com.tourapp.dto;
import lombok.*;
import java.util.List;
import java.util.stream.Collectors;

import com.tourapp.entity.AppUser;
import com.tourapp.entity.Booking;
import com.tourapp.entity.Role;
import com.tourapp.entity.Tour;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private Long id;
    private String status;
    private Long touristId;
    private String touristName;
    private Long tourId;
    private String tourTitle;

    // Constructor chuyển đổi từ Booking
    public BookingDTO(Booking booking) {
        this.id = booking.getId();
        this.status = booking.getStatus();
        if (booking.getTourist() != null) {
            this.touristId = booking.getTourist().getId();
            this.touristName = booking.getTourist().getName();
        }
        if (booking.getTour() != null) {
            this.tourId = booking.getTour().getId();
            this.tourTitle = booking.getTour().getTitle();
        }
    }

    // Chuyển đổi từ DTO về Entity
    public Booking toEntity(AppUser tourist, Tour tour) {
        Booking booking = new Booking();
        booking.setId(this.id);
        booking.setStatus(this.status);
        booking.setTourist(tourist);
        booking.setTour(tour);
        return booking;
    }

    // Chuyển đổi danh sách Booking thành danh sách BookingDTO
    public static List<BookingDTO> fromEntityList(List<Booking> bookings) {
        return bookings.stream().map(BookingDTO::new).collect(Collectors.toList());
    }
}
