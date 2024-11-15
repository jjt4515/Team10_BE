package poomasi.domain.farm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poomasi.domain.farm.entity.FarmInfo;
import poomasi.domain.farm.repository.FarmInfoRepository;
import poomasi.global.error.BusinessException;

import java.util.Collection;
import java.util.List;

import static poomasi.global.error.BusinessError.FARM_INFO_MAIN_REQUIRED_NO_CONTENT;
import static poomasi.global.error.BusinessError.FARM_INFO_NON_MAIN_REQUIRED_CONTENT;

@Service
@RequiredArgsConstructor
public class FarmInfoService {
    private final FarmInfoRepository farmInfoRepository;

    public Long saveFarmInfo(FarmInfo farmInfo) {
        // 메인 사진인데 내용이 있을 경우 예외 처리
        if (farmInfo.isMain() && farmInfo.hasContent()) {
            throw new BusinessException(FARM_INFO_MAIN_REQUIRED_NO_CONTENT);
        } else if (!farmInfo.isMain() && !farmInfo.hasContent()) {
            throw new BusinessException(FARM_INFO_NON_MAIN_REQUIRED_CONTENT);
        }

        return farmInfoRepository.save(farmInfo).getId();
    }

    public FarmInfo getFarmInfo(Long farmId) {
        return farmInfoRepository.findById(farmId).orElseThrow();
    }

    public List<FarmInfo> getFarmInfoByFarmId(Long farmId) {
        return farmInfoRepository.findAllByFarmId(farmId);
    }

    public void deleteFarmInfoById(Long farmInfoId) {
        farmInfoRepository.deleteById(farmInfoId);
    }

    public void deleteFarmInfo(Long farmId) {
        farmInfoRepository.deleteAllByFarmId(farmId);
    }

    public List<FarmInfo> getFarmSchedulesByFarmId(Long farmId) {
        return farmInfoRepository.findAllByFarmId(farmId);
    }
}
