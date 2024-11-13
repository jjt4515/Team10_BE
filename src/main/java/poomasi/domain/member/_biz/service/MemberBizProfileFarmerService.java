package poomasi.domain.member._biz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poomasi.domain.member._biz.dto.request.BizProfileCreateRequest;
import poomasi.domain.member.entity.Member;

@Service
@RequiredArgsConstructor
public class MemberBizProfileFarmerService {
    private final MemberBizProfileService memberBizProfileService;

    public Long updateBizProfile(Member member, BizProfileCreateRequest request) {

        return memberBizProfileService.save(request.toEntity(member.getId())).getId();
    }
}
