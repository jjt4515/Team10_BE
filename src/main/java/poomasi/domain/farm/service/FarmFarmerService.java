package poomasi.domain.farm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poomasi.domain.farm.dto.FarmRegisterRequest;
import poomasi.domain.farm.dto.FarmUpdateRequest;
import poomasi.domain.farm.entity.Farm;
import poomasi.domain.farm.repository.FarmRepository;
import poomasi.domain.member.entity.Member;
import poomasi.global.error.BusinessException;

import static poomasi.global.error.BusinessError.*;

@Service
@RequiredArgsConstructor
public class FarmFarmerService {
    private final FarmRepository farmRepository;

    public Long registerFarm(Member member, FarmRegisterRequest request) {

        farmRepository.getFarmByOwnerIdAndDeletedAtIsNull(member.getId()).ifPresent(farm -> {
            throw new BusinessException(FARM_ALREADY_EXISTS);
        });

        return farmRepository.save(request.toEntity(member.getId())).getId();

    }

    public Long updateFarm(Long farmerId, FarmUpdateRequest request) {
        Farm farm = this.getFarmByFarmId(request.farmId());

        if (!farm.getOwnerId().equals(farmerId)) {
            throw new BusinessException(FARM_OWNER_MISMATCH);
        }

        return farmRepository.save(request.toEntity(farm)).getId();
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
