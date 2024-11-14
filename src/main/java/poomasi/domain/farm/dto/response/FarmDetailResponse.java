package poomasi.domain.farm.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record FarmDetailResponse(
        FarmResponse farmResponse,
        List<FarmInfoResponse> experienceResponses
) {


}
