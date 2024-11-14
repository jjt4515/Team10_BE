package poomasi.domain.image.dto;

import poomasi.domain.image.entity.Image;
import poomasi.domain.image.entity.ImageType;

import java.time.LocalDateTime;

public record ImageResponse(
        Long id,
        String objectKey,
        String imageUrl,
        ImageType type,
        Long referenceId,
        LocalDateTime createdAt,
        LocalDateTime deletedAt
) {
    public static ImageResponse fromEntity(Image image) {
        return new ImageResponse(
                image.getId(),
                image.getObjectKey(),
                image.getImageUrl(),
                image.getType(),
                image.getReferenceId(),
                image.getCreatedAt(),
                image.getDeletedAt()
        );
    }
}
