package poomasi.domain.member._biz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poomasi.domain.member._biz.entity.MemberBizProfile;
import poomasi.domain.member._biz.repository.MemberBizProfileRepository;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberBizProfileService {
    private final MemberBizProfileRepository memberBizProfileRepository;

    public MemberBizProfile save(MemberBizProfile memberBizProfile) {
        return memberBizProfileRepository.save(memberBizProfile);
    }

    public MemberBizProfile findByMemberId(Long memberId) {
        return memberBizProfileRepository.findByMemberId(memberId).orElseThrow(
                () -> new BusinessException(BusinessError.MEMBER_BIZ_PROFILE_NOT_FOUND)
        );
    }
}
