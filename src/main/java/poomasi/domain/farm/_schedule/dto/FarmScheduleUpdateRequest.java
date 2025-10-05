package poomasi.domain.farm._schedule.dto;

import jakarta.validation.constraints.NotNull;
import poomasi.domain.farm._schedule.entity.FarmSchedule;

import java.time.LocalDate;
import java.time.LocalTime;

public record FarmScheduleUpdateRequest(
        Long farmId,
        @NotNull(message = "날짜는 필수 값입니다.")
        LocalDate date,
        @NotNull(message = "시작 시간은 필수 값입니다.")
        LocalTime startTime,
        @NotNull(message = "종료 시간은 필수 값입니다.")
        LocalTime endTime
) {
    public FarmSchedule toEntity() {
        return FarmSchedule.builder()
                .farmId(farmId)
                .startTime(startTime)
                .endTime(endTime)
                .date(date)
                .build();
    }
}
