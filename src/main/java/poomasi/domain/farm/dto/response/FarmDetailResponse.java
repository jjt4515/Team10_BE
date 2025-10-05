package poomasi.domain.farm.dto.response;

import lombok.Builder;
import poomasi.domain.farm._schedule.dto.FarmScheduleResponse;

import java.util.List;

@Builder
public record FarmDetailResponse(
        FarmResponse farmResponse,
        FarmInfoAggregateResponse info,
        List<FarmScheduleResponse> schedules
) {


}
