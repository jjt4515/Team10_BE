package poomasi.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.member.dto.request.FarmerUpdateRequest;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.repository.MemberRepository;
import poomasi.domain.store.entity.Store;
import poomasi.global.error.BusinessException;

import static poomasi.domain.member.entity.Role.ROLE_FARMER;
import static poomasi.global.error.BusinessError.INVALID_ROLE;
import static poomasi.global.error.BusinessError.MEMBER_ALREADY_FARMER;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberFarmerService {
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Transactional
    public void convertToFarmer(Member member) {
        if (member.isFarmer()) {
            throw new BusinessException(MEMBER_ALREADY_FARMER);
        }

        member.setAddress(null, null, null, null);
        member.setRole(ROLE_FARMER);
        memberRepository.save(member);
    }

    @Transactional
    public Member updateFarmer(Member member, FarmerUpdateRequest farmerUpdateRequest)
    {
        if (!member.isFarmer()) {
            throw new BusinessException(INVALID_ROLE);
        }

        memberService.updateCommonAttributes(member, farmerUpdateRequest.name(), farmerUpdateRequest.email(), farmerUpdateRequest.password(), farmerUpdateRequest.phoneNumber());

        Store store = member.getOrCreateStore();

        if (farmerUpdateRequest.storeName() != null) {
            store.setName(farmerUpdateRequest.storeName());
        }
        if (farmerUpdateRequest.storeAddress() != null) {
            store.setAddress(farmerUpdateRequest.storeAddress());
        }

        return memberRepository.save(member);
    }
}
