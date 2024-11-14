package poomasi.domain.farm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.farm.dto.request.FarmInfoRegisterRequest;
import poomasi.domain.farm.dto.request.FarmInfoUpdateRequest;
import poomasi.domain.farm.dto.request.FarmRegisterRequest;
import poomasi.domain.farm.dto.request.FarmUpdateRequest;
import poomasi.domain.farm.entity.Farm;
import poomasi.domain.farm.entity.FarmInfo;
import poomasi.domain.farm.repository.FarmRepository;
import poomasi.domain.member.entity.Member;
import poomasi.global.error.BusinessException;

import java.util.List;

import static poomasi.global.error.BusinessError.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FarmFarmerService {
    private final FarmRepository farmRepository;
    private final FarmService farmService;
    private final FarmInfoService farmInfoService;

    private final int MAX_FARM_INFO_COUNT = 4;

    @Transactional
    public Long registerFarm(Member member, FarmRegisterRequest request) {
        farmRepository.getFarmByOwnerIdAndDeletedAtIsNull(member.getId()).ifPresent(farm -> {
            throw new BusinessException(FARM_ALREADY_EXISTS);
        });

        Long id = farmRepository.save(request.toEntity(member.getId())).getId();

        List<FarmInfoRegisterRequest> farmInfoRegisterRequests = request.info().toRequest();
        farmInfoRegisterRequests.forEach(farmInfoRegisterRequest -> {
            registerFarmInfo(member, farmInfoRegisterRequest);
        });

        return id;
    }


    @Transactional
    public void registerFarmInfo(Member member, FarmInfoRegisterRequest request) {
        Farm farm = farmService.getFarmByFarmerId(member.getId());

        List<FarmInfo> farmInfos = farmInfoService.getFarmInfoByFarmId(farm.getId()).stream()
                .filter(FarmInfo::isValid)
                .toList();

        if (farmInfos.size() > MAX_FARM_INFO_COUNT) {
            throw new BusinessException(FARM_INFO_LIMIT_EXCEEDED);
        }

        if (request.isMain() && farmInfos.stream().anyMatch(FarmInfo::isMain)) {
            throw new BusinessException(FARM_INFO_MAIN_ALREADY_EXISTS);
        } else if (!request.isMain() && farmInfos.stream().noneMatch(FarmInfo::isMain) && farmInfos.size() == MAX_FARM_INFO_COUNT - 1) {
            throw new BusinessException(FARM_INFO_MAIN_REQUIRED);
        }

        farmInfoService.saveFarmInfo(request.toEntity(farm.getId()));
    }

    @Transactional
    public Long updateFarm(Long farmerId, FarmUpdateRequest request) {
        Farm farm = farmService.getFarmByFarmerId(farmerId);

        if (!farm.getOwnerId().equals(farmerId)) {
            throw new BusinessException(FARM_OWNER_MISMATCH);
        }

        return farmRepository.save(request.toEntity(farm)).getId();
    }


    public void updateFarmExpPrice(Long farmerId, Long farmId, int expPrice) {
        Farm farm = farmService.getFarmByFarmId(farmId);
        if (!farm.getOwnerId().equals(farmerId)) {
            throw new BusinessException(FARM_OWNER_MISMATCH);
        }
        farm.updateExpPrice(expPrice);
    }

    public void updateFarmMaxCapacity(Long farmerId, Long farmId, Integer maxCapacity) {
        Farm farm = farmService.getFarmByFarmId(farmId);
        if (!farm.getOwnerId().equals(farmerId)) {
            throw new BusinessException(FARM_OWNER_MISMATCH);
        }
        farm.updateMaxCapacity(maxCapacity);
    }

    public void updateFarmMaxReservation(Long farmerId, Long farmId, Integer maxReservation) {
        Farm farm = farmService.getFarmByFarmId(farmId);
        if (!farm.getOwnerId().equals(farmerId)) {
            throw new BusinessException(FARM_OWNER_MISMATCH);
        }
        farm.updateMaxReservation(maxReservation);
    }

    public void deleteFarm(Long farmerId, Long farmId) {
        Farm farm = farmService.getFarmByFarmId(farmId);

        // farm이 null인 경우 예외 발생
        if (farm == null) {
            throw new BusinessException(FARM_NOT_FOUND);
        }

        if (!farm.getOwnerId().equals(farmerId)) {
            throw new BusinessException(FARM_OWNER_MISMATCH);
        }

        if (farm.getDeletedAt() != null) {
            throw new BusinessException(FARM_ALREADY_DELETED);
        }

        farmService.delete(farm);
        farmInfoService.deleteFarmInfo(farmId);
    }

    public Long updateFarmInfo(Member member, FarmInfoUpdateRequest request) {
        Farm farm = farmService.getFarmByFarmerId(member.getId());

        if (!farm.getOwnerId().equals(member.getId())) {
            throw new BusinessException(FARM_OWNER_MISMATCH);
        }

        FarmInfo farmInfo = farmInfoService.getFarmInfo(request.farmId());
        farmInfo.update(request);
        return farmInfoService.saveFarmInfo(farmInfo);
    }
}
