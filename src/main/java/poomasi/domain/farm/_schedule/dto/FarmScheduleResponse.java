package poomasi.domain.farm._schedule.dto;

import lombok.Builder;
import poomasi.domain.farm._schedule.entity.FarmSchedule;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record FarmScheduleResponse(
        Long scheduleId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {

    public static FarmScheduleResponse fromEntity(FarmSchedule farmSchedule) {
        return FarmScheduleResponse.builder()
                .scheduleId(farmSchedule.getId())
                .startTime(farmSchedule.getStartTime())
                .endTime(farmSchedule.getEndTime())
                .date(farmSchedule.getDate())
                .build();
    }
}
