package poomasi.domain.farm._schedule.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import poomasi.domain.farm._schedule.entity.FarmSchedule;
import poomasi.domain.farm._schedule.entity.ScheduleStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record FarmScheduleUpdateRequest(
        Long farmId,
        @NotNull(message = "날짜는 필수 값입니다.")
        LocalDate date,
        @NotNull(message = "시작 시간은 필수 값입니다.")
        LocalTime startTime,
        @NotNull(message = "종료 시간은 필수 값입니다.")
        LocalTime endTime,


        @NotEmpty(message = "예약 가능한 요일은 필수 값입니다.")
        List<DayOfWeek> availableDays // 예약 가능한 요일 리스트
) {
    public FarmSchedule toEntity() {
        return FarmSchedule.builder()
                .farmId(farmId)
                .date(date)
                .build();
    }
}
