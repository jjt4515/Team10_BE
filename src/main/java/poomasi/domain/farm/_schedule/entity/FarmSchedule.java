package poomasi.domain.farm._schedule.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import java.time.LocalTime;
import java.util.Collection;

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

    @Builder
    public FarmSchedule(Long farmId, LocalDate date, LocalTime startTime, LocalTime endTime, ScheduleStatus status) {
        this.farmId = farmId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
