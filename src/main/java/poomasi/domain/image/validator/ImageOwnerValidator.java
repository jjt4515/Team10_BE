package poomasi.domain.image.validator;

import poomasi.domain.image.entity.ImageType;

public interface ImageOwnerValidator {
    boolean validateOwner(Long memberId, Long referenceId);
    boolean supports(ImageType type);

}