package com.tourapp.dto;
import lombok.*;
import java.util.List;
import java.util.stream.Collectors;

import com.tourapp.entity.Tour;
import com.tourapp.entity.AppUser;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TourDTO {
    private Long id;
    private String title;
    private String description;
    private double price;
    private String location;
    private String thumbnail_0;
    private String thumbnail_1;
    private String thumbnail_2;
    private String thumbnail_3;
    private String thumbnail_4;
    private String thumbnail_5;
    private Long guideId;
    private String guideName;

    // Constructor chuyển đổi từ Tour
    public TourDTO(Tour tour) {
        this.id = tour.getId();
        this.title = tour.getTitle();
        this.description = tour.getDescription();
        this.price = tour.getPrice();
        this.location = tour.getLocation();
        this.thumbnail_0 = tour.getThumbnail_0();
        this.thumbnail_1 = tour.getThumbnail_1();
        this.thumbnail_2 = tour.getThumbnail_2();
        this.thumbnail_3 = tour.getThumbnail_3();
        this.thumbnail_4 = tour.getThumbnail_4();
        this.thumbnail_5 = tour.getThumbnail_5();
        if (tour.getGuide() != null) {
            this.guideId = tour.getGuide().getId();
            this.guideName = tour.getGuide().getName();
        }
    }

    // Chuyển đổi từ DTO về Entity
    public Tour toEntity(AppUser guide) {
        Tour tour = new Tour();
        tour.setId(this.id);
        tour.setTitle(this.title);
        tour.setDescription(this.description);
        tour.setPrice(this.price);
        tour.setLocation(this.location);
        tour.setThumbnail_0(this.thumbnail_0);
        tour.setThumbnail_1(this.thumbnail_1);
        tour.setThumbnail_2(this.thumbnail_2);
        tour.setThumbnail_3(this.thumbnail_3);
        tour.setThumbnail_4(this.thumbnail_4);
        tour.setThumbnail_5(this.thumbnail_5);
        tour.setGuide(guide);
        return tour;
    }

    // Chuyển đổi danh sách Tour thành danh sách TourDTO
    public static List<TourDTO> fromEntityList(List<Tour> tours) {
        return tours.stream().map(TourDTO::new).collect(Collectors.toList());
    }
}