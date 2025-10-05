package poomasi.domain.farm.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import poomasi.domain.farm.entity.FarmInfo;

import java.util.List;

public record FarmInfoAggregateResponse(
        @NotBlank(message = "제목은 필수 입력값입니다.")
        String title,

        @NotBlank(message = "대표 이미지는 필수 입력값입니다.")
        String mainImage,

        @NotNull(message = "세부 제목 리스트는 null일 수 없습니다.")
        @Size(min = 3, max = 3, message = "세부 제목은 3개여야 합니다.")
        List<String> detailTitles,

        @NotNull(message = "세부 설명 리스트는 null일 수 없습니다.")
        List<String> detailDescriptions,

        @NotNull(message = "세부 이미지 리스트는 null일 수 없습니다.")
        List<String> detailImages
) {
    public static FarmInfoAggregateResponse fromEntity(List<FarmInfo> farmInfos) {
        return new FarmInfoAggregateResponse(
                farmInfos.get(0).getTitle(),
                farmInfos.get(0).getImageUrl(),
                List.of(farmInfos.get(1).getTitle(), farmInfos.get(2).getTitle(), farmInfos.get(3).getTitle()),
                List.of(farmInfos.get(1).getContent(), farmInfos.get(2).getContent(), farmInfos.get(3).getContent()),
                List.of(farmInfos.get(1).getImageUrl(), farmInfos.get(2).getImageUrl(), farmInfos.get(3).getImageUrl())
        );
    }
}
