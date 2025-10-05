package poomasi.domain.image.deleteLinker;

import org.springframework.stereotype.Component;
import poomasi.domain.image.entity.ImageType;

import java.util.List;

@Component
public class ImageDeleteFactory {

    private final List<ImageDeleteLinker> deleteLinkers;

    public ImageDeleteFactory(List<ImageDeleteLinker> deleteLinkers) {
        this.deleteLinkers = deleteLinkers;
    }

    public ImageDeleteLinker getDeleteLinker(ImageType type) {
        return deleteLinkers.stream()
                .filter(linker -> linker.supports(type))
                .findFirst()
                .orElse(null);
    }
}
