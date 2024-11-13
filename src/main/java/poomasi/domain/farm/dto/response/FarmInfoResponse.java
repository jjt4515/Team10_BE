package poomasi.domain.farm.dto.response;

import poomasi.domain.farm.entity.FarmInfo;

public record FarmInfoResponse(
        Long id,
        String imageUrl,
        String title,
        String content,
        boolean isMain
) {

    public static FarmInfoResponse fromEntity(FarmInfo farmInfo) {
        return new FarmInfoResponse(
                farmInfo.getId(),
                farmInfo.getImageUrl(),
                farmInfo.getTitle(),
                farmInfo.getContent(),
                farmInfo.isMain()
        );
    }
}
