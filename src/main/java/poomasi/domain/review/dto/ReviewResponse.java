package poomasi.domain.review.dto;

import java.util.List;
import poomasi.domain.image.entity.Image;
import poomasi.domain.review.entity.Review;

public record ReviewResponse
        (Long id,
         Long entityId,
         String reviewerName,
         Float rating,
         String content,
         List<String> imageUrls
        ) {

    public static ReviewResponse fromEntity(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getEntityId(),
                review.getReviewer().getName() == null ? "" : review.getReviewer().getName(),
                review.getRating(),
                review.getContent(),
                review.getImages().stream().map(Image::getImageUrl).toList()
        );
    }
}
