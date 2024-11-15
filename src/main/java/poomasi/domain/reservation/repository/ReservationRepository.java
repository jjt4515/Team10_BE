package poomasi.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poomasi.domain.farm._schedule.entity.FarmSchedule;
import poomasi.domain.farm.entity.Farm;
import poomasi.domain.reservation.entity.Reservation;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByFarmId(Long farmId);

    List<Reservation> findAllByMemberId(Long memberId);

    List<Reservation> findAllByFarmIdAndScheduleId(Long farm_id, FarmSchedule scheduleId);

    List<Reservation> findAllByFarmIn(List<Farm> farms);
}
