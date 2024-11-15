package poomasi.domain.farm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import poomasi.global.error.BusinessException;

import java.util.ArrayList;
import java.util.List;

import static poomasi.global.error.BusinessError.FARM_INFO_DETAIL_SIZE_MISMATCH;

@Builder
public record FarmInfoAggregateRequest(
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
    public List<FarmInfoRegisterRequest> toRequest() {
        validateListsSize();

        List<FarmInfoRegisterRequest> requests = new ArrayList<>();

        requests.add(new FarmInfoRegisterRequest(
                true,
                title,
                null,
                mainImage
        ));

        // Add detailed farm info requests
        for (int i = 0; i < detailTitles.size(); i++) {
            String detailTitle = detailTitles.get(i);
            String detailDescription = (i < detailDescriptions.size()) ? detailDescriptions.get(i) : null;
            String detailImage = (i < detailImages.size()) ? detailImages.get(i) : null;

            requests.add(new FarmInfoRegisterRequest(
                    false,
                    detailTitle,
                    detailDescription,
                    detailImage
            ));
        }

        return requests;
    }

    private void validateListsSize() {
        if (detailDescriptions.size() != detailTitles.size()) {
            throw new BusinessException(FARM_INFO_DETAIL_SIZE_MISMATCH);
        }
        if (detailImages.size() != detailTitles.size()) {
            throw new BusinessException(FARM_INFO_DETAIL_SIZE_MISMATCH);
        }
    }
}
