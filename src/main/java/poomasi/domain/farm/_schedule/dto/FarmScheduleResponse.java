package poomasi.domain.farm._schedule.dto;

import java.time.LocalDate;
import lombok.Builder;
import poomasi.domain.farm._schedule.entity.FarmSchedule;
import poomasi.domain.farm._schedule.entity.ScheduleStatus;

@Builder
public record FarmScheduleResponse(
        LocalDate date,
        ScheduleStatus status
) {

    public static FarmScheduleResponse fromEntity(FarmSchedule farmSchedule) {
        return FarmScheduleResponse.builder()
                .date(farmSchedule.getDate())
                .status(farmSchedule.getStatus())
                .build();
    }
}
