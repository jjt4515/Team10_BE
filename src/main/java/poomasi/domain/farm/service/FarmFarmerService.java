package poomasi.domain.farm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poomasi.domain.farm.dto.request.FarmInfoRegisterRequest;
import poomasi.domain.farm.dto.request.FarmRegisterRequest;
import poomasi.domain.farm.dto.request.FarmUpdateRequest;
import poomasi.domain.farm.entity.Farm;
import poomasi.domain.farm.entity.FarmInfo;
import poomasi.domain.farm.repository.FarmRepository;
import poomasi.domain.member.entity.Member;
import poomasi.global.error.BusinessException;

import java.util.List;
import java.util.Objects;

import static poomasi.global.error.BusinessError.*;

@Service
@RequiredArgsConstructor
public class FarmFarmerService {
    private final FarmRepository farmRepository;
    private final FarmInfoService farmInfoService;

    private final int MAX_FARM_INFO_COUNT = 4;

    public Long registerFarm(Member member, FarmRegisterRequest request) {

        farmRepository.getFarmByOwnerIdAndDeletedAtIsNull(member.getId()).ifPresent(farm -> {
            throw new BusinessException(FARM_ALREADY_EXISTS);
        });

        return farmRepository.save(request.toEntity(member.getId())).getId();
    }

    public Long registerFarmInfo(Member member, FarmInfoRegisterRequest request) {
        Farm farm = getFarmByFarmerId(member.getId());

        List<FarmInfo> farmInfos = farmInfoService.getFarmInfoByFarmId(farm.getId()).stream()
                .filter(FarmInfo::isValid)
                .toList();

        if (farmInfos.size() >= MAX_FARM_INFO_COUNT) {
            throw new BusinessException(FARM_INFO_LIMIT_EXCEEDED);
        }

        if (request.isMain() && farmInfos.stream().anyMatch(FarmInfo::isMain)) {
            throw new BusinessException(FARM_INFO_MAIN_ALREADY_EXISTS);
        } else if (!request.isMain() && farmInfos.stream().noneMatch(FarmInfo::isMain) && farmInfos.size() == MAX_FARM_INFO_COUNT - 1) {
            throw new BusinessException(FARM_INFO_MAIN_REQUIRED);
        }

        return farmInfoService.saveFarmInfo(request.toEntity(farm.getId()));
    }

    public Long updateFarm(Long farmerId, FarmUpdateRequest request) {
        Farm farm = getFarmByFarmerId(farmerId);

        if (!farm.getOwnerId().equals(farmerId)) {
            throw new BusinessException(FARM_OWNER_MISMATCH);
        }

        return farmRepository.save(request.toEntity(farm)).getId();
    }

    public Farm getFarmByFarmerId(Long farmerId) {
        return farmRepository.getFarmByOwnerIdAndDeletedAtIsNull(farmerId).orElseThrow(() -> new BusinessException(FARM_NOT_FOUND));
    }

    private Farm getFarmByFarmId(Long farmId) {
        return farmRepository.findByIdAndDeletedAtIsNull(farmId).orElseThrow(() -> new BusinessException(FARM_NOT_FOUND));
    }

    public void updateFarmExpPrice(Long farmerId, Long farmId, int expPrice) {
        Farm farm = this.getFarmByFarmId(farmId);
        if (!farm.getOwnerId().equals(farmerId)) {
            throw new BusinessException(FARM_OWNER_MISMATCH);
        }
        farm.updateExpPrice(expPrice);
    }

    public void updateFarmMaxCapacity(Long farmerId, Long farmId, Integer maxCapacity) {
        Farm farm = this.getFarmByFarmId(farmId);
        if (!farm.getOwnerId().equals(farmerId)) {
            throw new BusinessException(FARM_OWNER_MISMATCH);
        }
        farm.updateMaxCapacity(maxCapacity);
    }

    public void updateFarmMaxReservation(Long farmerId, Long farmId, Integer maxReservation) {
        Farm farm = this.getFarmByFarmId(farmId);
        if (!farm.getOwnerId().equals(farmerId)) {
            throw new BusinessException(FARM_OWNER_MISMATCH);
        }
        farm.updateMaxReservation(maxReservation);
    }

    public void deleteFarm(Long farmerId, Long farmId) {
        Farm farm = this.getFarmByFarmId(farmId);
        if (!farm.getOwnerId().equals(farmerId)) {
            throw new BusinessException(FARM_OWNER_MISMATCH);
        }
        farmRepository.delete(farm);
    }
}
