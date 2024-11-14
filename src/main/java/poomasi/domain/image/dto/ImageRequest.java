package poomasi.domain.image.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import poomasi.domain.image.entity.Image;
import poomasi.domain.image.entity.ImageType;

public record ImageRequest(
        @NotBlank(message = "Object key이 blank입니다.")
        String objectKey,

        @NotBlank(message = "Image URL이 blank입니다.")
        String imageUrl,

        @NotNull(message = "Image type이 null입니다.")
        ImageType type,

        @NotNull(message = "Reference ID가 null입니다.")
        @Positive(message = "Reference ID는 양의 정수이어야 합니다.")
        Long referenceId
) {
    public Image toEntity(ImageRequest imageRequest){
        return new Image(
                imageRequest.objectKey,
                imageRequest.imageUrl,
                imageRequest.type,
                imageRequest.referenceId // 타입이 멤버 프로필일 경우 멤버 id가 아닌 멤버 프로필 id를 넣습니다.
        );
    }
}

