package poomasi.domain.farm._schedule.dto;

import java.time.LocalDate;
import lombok.Builder;
import poomasi.domain.farm._schedule.entity.FarmSchedule;
import poomasi.domain.farm._schedule.entity.ScheduleStatus;

=======
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record FarmScheduleResponse(
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        ScheduleStatus status
) {

    public static FarmScheduleResponse fromEntity(FarmSchedule farmSchedule) {
        return FarmScheduleResponse.builder()
                .startTime(farmSchedule.getStartTime())
                .endTime(farmSchedule.getEndTime())
                .date(farmSchedule.getDate())
                .build();
    }
}
