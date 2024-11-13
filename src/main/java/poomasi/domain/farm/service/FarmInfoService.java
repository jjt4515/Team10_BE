package poomasi.domain.farm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poomasi.domain.farm.entity.FarmInfo;
import poomasi.domain.farm.repository.FarmInfoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FarmInfoService {
    private final FarmInfoRepository farmInfoRepository;
    
    public Long saveFarmInfo(FarmInfo farmInfo) {
        return farmInfoRepository.save(farmInfo).getId();
    }

    public FarmInfo getFarmInfo(Long farmId) {
        return farmInfoRepository.findById(farmId).orElseThrow();
    }

    public List<FarmInfo> getFarmInfoByFarmId(Long farmId) {
        return farmInfoRepository.findAllByFarmId(farmId);
    }
}
