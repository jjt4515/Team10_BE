package poomasi.domain.member._biz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poomasi.domain.member._biz.entity.MemberBizProfile;

@Repository
public interface MemberBizProfileRepository extends JpaRepository<MemberBizProfile, Long> {
}
