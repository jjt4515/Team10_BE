package poomasi.domain.image.linker;

import org.springframework.stereotype.Component;
import poomasi.domain.image.entity.ImageType;
import poomasi.global.error.BusinessException;

import java.util.List;

import static poomasi.global.error.BusinessError.IMAGE_TYPE_NOT_FOUND;

@Component
public class ImageLinkerFactory {

    private final List<ImageLinker> linkers;

    public ImageLinkerFactory(List<ImageLinker> linkers) {
        this.linkers = linkers;
    }

    public ImageLinker getLinker(ImageType type) {
        return linkers.stream()
                .filter(linker -> linker.supports(type))
                .findFirst()
                .orElseThrow(() -> new BusinessException(IMAGE_TYPE_NOT_FOUND));
    }
}