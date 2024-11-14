package poomasi.domain.farm.dto.response;

import lombok.Builder;

@Builder
public record FarmDetailResponse(
        FarmResponse farmResponse,
        FarmInfoAggregateResponse info
) {


}
