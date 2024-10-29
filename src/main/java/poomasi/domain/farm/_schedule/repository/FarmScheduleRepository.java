package poomasi.domain.farm._schedule.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import poomasi.domain.farm._schedule.entity.FarmSchedule;

@Repository
public interface FarmScheduleRepository extends JpaRepository<FarmSchedule, Long> {

    @Query("SELECT f FROM FarmSchedule f WHERE f.farmId = :farmId AND f.date BETWEEN :startDate AND :endDate")
    List<FarmSchedule> findByFarmIdAndDateRange(Long farmId, LocalDate startDate,
            LocalDate endDate);

    Optional<FarmSchedule> findByFarmIdAndDate(Long aLong, LocalDate date);

}
