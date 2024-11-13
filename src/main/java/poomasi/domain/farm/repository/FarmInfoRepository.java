package poomasi.domain.farm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poomasi.domain.farm.entity.FarmInfo;

import java.util.List;

@Repository
public interface FarmInfoRepository extends JpaRepository<FarmInfo, Long> {
    List<FarmInfo> findAllByFarmId(Long farmId);
}
