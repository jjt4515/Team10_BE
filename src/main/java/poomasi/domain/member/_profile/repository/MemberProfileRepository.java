package poomasi.domain.member._profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poomasi.domain.member._profile.entity.MemberProfile;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {
}