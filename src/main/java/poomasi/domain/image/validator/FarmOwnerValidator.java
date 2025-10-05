package poomasi.domain.image.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import poomasi.domain.farm.repository.FarmRepository;
import poomasi.domain.image.entity.ImageType;

@Component
@RequiredArgsConstructor
public class FarmOwnerValidator implements ImageOwnerValidator {
    private final FarmRepository farmRepository;

    @Override
    public boolean supports(ImageType type) {
        return type == ImageType.FARM;
    }

    @Override
    public boolean validateOwner(Long memberId, Long referenceId) {
        return farmRepository.findByIdAndDeletedAtIsNull(referenceId)
                .filter(farm -> farm.getOwnerId().equals(memberId))
                .isPresent();
    }
}
