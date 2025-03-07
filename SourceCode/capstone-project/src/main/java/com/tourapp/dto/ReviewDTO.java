package com.tourapp.dto;
import lombok.*;
import java.util.List;
import java.util.stream.Collectors;

import com.tourapp.entity.Review;
import com.tourapp.entity.Tour;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private Long id;
    private int rating;
    private String comment;
    private Long tourId;

    // Constructor chuyển đổi từ Review
    public ReviewDTO(Review review) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        if (review.getTour() != null) {
            this.tourId = review.getTour().getId();
        }
    }

    // Chuyển đổi từ DTO về Entity
    public Review toEntity(Tour tour) {
        Review review = new Review();
        review.setId(this.id);
        review.setRating(this.rating);
        review.setComment(this.comment);
        review.setTour(tour);
        return review;
    }

    // Chuyển đổi danh sách Review thành danh sách ReviewDTO
    public static List<ReviewDTO> fromEntityList(List<Review> reviews) {
        return reviews.stream().map(ReviewDTO::new).collect(Collectors.toList());
    }
}