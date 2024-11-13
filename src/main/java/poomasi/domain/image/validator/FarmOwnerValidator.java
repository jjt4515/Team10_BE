package poomasi.domain.image.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import poomasi.domain.farm.repository.FarmRepository;

@Component
@RequiredArgsConstructor
public class FarmOwnerValidator implements ImageOwnerValidator {
    private final FarmRepository farmRepository;

    @Override
    public boolean validateOwner(Long memberId, Long referenceId) {
        return farmRepository.findByIdAndDeletedAtIsNull(referenceId)
                .filter(farm -> farm.getOwnerId().equals(memberId))
                .isPresent();
    }
}
