package poomasi.domain.image.deleteLinker;

import poomasi.domain.image.entity.Image;
import poomasi.domain.image.entity.ImageType;

public interface ImageDeleteLinker {
    boolean supports(ImageType type);
    void handleImageDeletion(Image image);
}