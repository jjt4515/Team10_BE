package poomasi.domain.review.dto;

import poomasi.domain.image.dto.response.ImageResponse;
import poomasi.domain.image.entity.Image;
import poomasi.domain.review.entity.Review;

import java.util.List;

public record ReviewResponse
        (Long id,
         Long entityId,
         String reviewerName,
         Float rating,
         String content,
         ImageResponse image
        ) {

    public static ReviewResponse fromEntity(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getEntityId(),
                review.getReviewer().getName() == null ? "" : review.getReviewer().getName(),
                review.getRating(),
                review.getContent(),
                ImageResponse.fromEntity(review.getImage())
        );
    }
}
