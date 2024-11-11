package poomasi.domain.image.dto;

import poomasi.domain.image.entity.Image;
import poomasi.domain.image.entity.ImageType;

public record ImageRequest(String objectKey, String imageUrl, ImageType type, Long referenceId) {
    public Image toEntity(ImageRequest imageRequest){
        return new Image(
                imageRequest.objectKey,
                imageRequest.imageUrl,
                imageRequest.type,
                imageRequest.referenceId // 타입이 멤버 프로필일 경우 멤버 id가 아닌 멤버 프로필 id를 넣습니다.
        );
    }
}

