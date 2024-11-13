package poomasi.domain.image.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import poomasi.domain.image.entity.ImageType;
import poomasi.domain.review.repository.ReviewRepository;

@Component
@RequiredArgsConstructor
public class ReviewOwnerValidator implements ImageOwnerValidator{
    private final ReviewRepository reviewRepository;

    @Override
    public boolean supports(ImageType type) {
        return type == ImageType.FARM_REVIEW || type == ImageType.PRODUCT_REVIEW;
    }

    @Override
    public boolean validateOwner(Long memberId, Long referenceId) {
        return reviewRepository.findById(referenceId)
                .filter(review -> review.getReviewer().getId().equals(memberId))
                .isPresent();
    }
}
