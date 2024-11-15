package poomasi.domain.image.validation;

public interface ImageOwnerValidator {
    boolean validateOwner(Long memberId, Long referenceId);
}