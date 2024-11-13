package poomasi.domain.farm.dto.request;

import jakarta.validation.constraints.NotNull;
import poomasi.domain.farm.entity.FarmInfo;

public record FarmInfoRegisterRequest(
        @NotNull
        boolean isMain,
        String title,
        String content,
        String imageUrl
) {
    public FarmInfo toEntity(Long id) {
        return FarmInfo.builder()
                .farmId(id)
                .isMain(isMain)
                .title(title)
                .content(content)
                .imageUrl(imageUrl)
                .build();
    }
}
