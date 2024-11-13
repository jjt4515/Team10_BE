package poomasi.domain.member._biz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poomasi.domain.member._biz.entity.MemberBizProfile;
import poomasi.domain.member._biz.repository.MemberBizProfileRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberBizProfileService {
    private final MemberBizProfileRepository memberBizProfileRepository;

    public MemberBizProfile save(MemberBizProfile memberBizProfile) {
        return memberBizProfileRepository.save(memberBizProfile);
    }

    public Optional<MemberBizProfile> findByMemberId(Long memberId) {
    }
}
