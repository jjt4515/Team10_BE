package poomasi.domain.image.validator;

import org.springframework.stereotype.Component;
import poomasi.domain.image.entity.ImageType;

import java.util.List;

@Component
public class ImageOwnerValidatorFactory {

    private final List<ImageOwnerValidator> validators;

    public ImageOwnerValidatorFactory(List<ImageOwnerValidator> validators) {
        this.validators = validators;
    }

    public ImageOwnerValidator getValidator(ImageType type) {
        return validators.stream()
                .filter(validator -> validator.supports(type))
                .findFirst()
                .orElse(null);
    }
}
