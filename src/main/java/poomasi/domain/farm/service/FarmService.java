package poomasi.domain.farm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poomasi.domain.farm.entity.Farm;
import poomasi.domain.farm.entity.FarmStatus;
import poomasi.domain.farm.repository.FarmRepository;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static poomasi.global.error.BusinessError.FARM_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FarmService {
    private final FarmRepository farmRepository;

    public List<Farm> getFarmListByOwnerId(Long farmerId) {
        return farmRepository.findAllByOwnerIdAndDeletedAtIsNull(farmerId);
    }

    public Farm getValidFarmByFarmId(Long farmId) {
        Farm farm = getFarmByFarmId(farmId);
        if (farm.getStatus() != FarmStatus.OPEN) {
            throw new BusinessException(BusinessError.FARM_NOT_OPEN);
        }
        return farm;
    }

    public Farm getFarmByFarmerId(Long farmerId) {
        return farmRepository.getFarmByOwnerIdAndDeletedAtIsNull(farmerId).orElseThrow(() -> new BusinessException(FARM_NOT_FOUND));
    }

    public Farm getFarmByFarmId(Long farmId) {
        return farmRepository.findByIdAndDeletedAtIsNull(farmId)
                .orElseThrow(() -> new BusinessException(BusinessError.FARM_NOT_FOUND));
    }

    public List<Farm> getFarmList(Pageable pageable) {
        return farmRepository.findByDeletedAtIsNull(pageable).stream()
                .collect(Collectors.toList());
    }

    public void delete(Farm farm) {
        farm.delete();
        farmRepository.save(farm);
    }

    public Collection<Farm> getFarmListByCategory(Long categoryId, Pageable pageable) {
        return farmRepository.findByCategoryIdAndDeletedAtIsNull(categoryId, pageable)
                .stream()
                .collect(Collectors.toList());
    }
}
