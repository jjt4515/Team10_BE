package poomasi.domain.member._biz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poomasi.domain.member._biz.entity.MemberBizProfile;

import java.util.Optional;

@Repository
public interface MemberBizProfileRepository extends JpaRepository<MemberBizProfile, Long> {
    Optional<MemberBizProfile> findByMemberId(Long memberId);
}
