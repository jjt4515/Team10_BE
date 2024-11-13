package poomasi.domain.image.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import poomasi.domain.image.entity.ImageType;
import poomasi.domain.product._intro.repository.ProductIntroRepository;

@Component
@RequiredArgsConstructor
public class  ProductIntroOwnerValidator  implements ImageOwnerValidator{
    private final ProductIntroRepository productIntroRepository;

    @Override
    public boolean supports(ImageType type) {
        return type == ImageType.PRODUCT_INTRO;
    }

    @Override
    public boolean validateOwner(Long memberId, Long referenceId) {
        return productIntroRepository.findById(referenceId)
                .filter(productIntro -> productIntro.getFarmerId().equals(memberId))
                .isPresent();
    }
}
