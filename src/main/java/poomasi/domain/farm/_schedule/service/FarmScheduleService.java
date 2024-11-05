package poomasi.domain.farm._schedule.service;

import static poomasi.global.error.BusinessError.FARM_NOT_FOUND;
import static poomasi.global.error.BusinessError.FARM_OWNER_MISMATCH;
import static poomasi.global.error.BusinessError.FARM_SCHEDULE_ALREADY_EXISTS;
import static poomasi.global.error.BusinessError.FARM_SCHEDULE_ALREADY_RESERVED;
import static poomasi.global.error.BusinessError.FARM_SCHEDULE_NOT_FOUND;
import static poomasi.global.error.BusinessError.START_DATE_SHOULD_BE_BEFORE_END_DATE;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poomasi.domain.farm._schedule.dto.FarmScheduleRequest;
import poomasi.domain.farm._schedule.dto.FarmScheduleResponse;
import poomasi.domain.farm._schedule.dto.FarmScheduleUpdateRequest;
import poomasi.domain.farm._schedule.entity.FarmSchedule;
import poomasi.domain.farm._schedule.repository.FarmScheduleRepository;
import poomasi.domain.farm.entity.Farm;
import poomasi.domain.farm.repository.FarmRepository;
import poomasi.domain.member.entity.Member;
import poomasi.global.error.BusinessException;
import java.time.LocalDate;
import java.util.List;
import static poomasi.global.error.BusinessError.*;

@Service
@RequiredArgsConstructor
public class FarmScheduleService {

    private final FarmScheduleRepository farmScheduleRepository;
    private final FarmRepository farmRepository;

    public void addFarmSchedule(FarmScheduleUpdateRequest request, Member member) {
        Farm farm = farmRepository.findById(request.farmId())
                .orElseThrow(() -> new BusinessException(FARM_NOT_FOUND));
        if (!farm.getOwnerId().equals(member.getId())) {
            throw new BusinessException(FARM_OWNER_MISMATCH);
        }

    public void addFarmSchedule(FarmScheduleUpdateRequest request) {
        if (request.startTime().isAfter(request.endTime())) {
            throw new BusinessException(START_TIME_SHOULD_BE_BEFORE_END_TIME);
        }

        // 이미 겹치는 예약이 존재하는지 확인
        List<FarmSchedule> farmSchedules = farmScheduleRepository.findByFarmIdAndDate(request.farmId(), request.date());
        if (farmSchedules.stream().anyMatch(farmSchedule -> {
            return (request.startTime().isBefore(farmSchedule.getEndTime()) && request.endTime().isAfter(farmSchedule.getStartTime()));
        })) {
            throw new BusinessException(FARM_SCHEDULE_ALREADY_EXISTS);
        }

        // 등록
        FarmSchedule farmSchedule = request.toEntity();
        farmScheduleRepository.save(farmSchedule);
    }

    public List<FarmScheduleResponse> getFarmSchedulesByYearAndMonth(FarmScheduleRequest request) {
        LocalDate startDate = LocalDate.of(request.year(), request.month(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return farmScheduleRepository.findByFarmIdAndDateRange(request.farmId(), startDate, endDate)
                .stream()
                .map(FarmScheduleResponse::fromEntity)
                .toList();
    }

    public List<FarmSchedule> getFarmScheduleByFarmIdAndDate(Long farmId, LocalDate date) {
        return farmScheduleRepository.findByFarmIdAndDate(farmId, date);
    }

    public FarmSchedule getFarmScheduleByScheduleId(Long id) {
        return farmScheduleRepository.findById(id).orElseThrow(
                () -> new BusinessException(FARM_SCHEDULE_NOT_FOUND)
        );
    }
}
