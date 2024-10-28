package poomasi.domain.farm._schedule.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Table(name = "farm_schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FarmSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("농장")
    private Long farmId;

    @Comment("예약 가능 날짜")
    private LocalDate date;

    @Comment("시작 시간")
    private LocalTime startTime;

    @Comment("종료 시간")
    private LocalTime endTime;

    @Comment("예약 상태")
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    @Comment("예약 가능 상태")
    private Boolean available;


    @Builder
    public FarmSchedule(Long farmId, LocalDate date, ScheduleStatus status) {
        this.farmId = farmId;
        this.date = date;
    }
}
