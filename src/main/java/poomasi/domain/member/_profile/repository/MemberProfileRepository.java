package poomasi.domain.member._profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poomasi.domain.member._profile.entity.FarmerProfile;
import poomasi.domain.member._profile.entity.MemberProfile;

import java.util.Optional;

@Repository
public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {

    // FarmerProfile 타입만 조회
//    @Query("SELECT p FROM FarmerProfile p WHERE p.farmName = :farmName")
//    Optional<FarmerProfile> findFarmerByFarmName(@Param("farmName") String farmName);

}