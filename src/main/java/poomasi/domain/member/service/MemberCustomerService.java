package poomasi.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.member._profile.dto.request.AddressUpdateRequest;
import poomasi.domain.member._profile.entity.MemberProfile;
import poomasi.domain.member.dto.request.CustomerUpdateRequest;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.repository.MemberRepository;
import poomasi.global.error.BusinessException;

import static poomasi.domain.member.entity.Role.ROLE_CUSTOMER;
import static poomasi.global.error.BusinessError.INVALID_ROLE;
import static poomasi.global.error.BusinessError.MEMBER_ALREADY_CUSTOMER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCustomerService {
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Transactional
    public void convertToCustomer(Long memberId) {
        Member member = memberService.findMemberById(memberId);

        if (member.isCustomer()) {
            throw new BusinessException(MEMBER_ALREADY_CUSTOMER);
        }

        member.setRole(ROLE_CUSTOMER);
        memberRepository.save(member);
    }

    @Transactional
    public Member updateCustomer(Member member, CustomerUpdateRequest customerUpdateRequest)
    {
        if (!member.isCustomer()) {
            throw new BusinessException(INVALID_ROLE);
        }

        memberService.updateCommonAttributes(member, customerUpdateRequest.name(),customerUpdateRequest.email(), customerUpdateRequest.password(), customerUpdateRequest.phoneNumber());

        return memberRepository.save(member);
    }

    @Transactional
    public void updateAddress(Member member, AddressUpdateRequest request) {
        MemberProfile profile = member.getOrCreateProfile();

        profile.setAddress(request.defaultAddress(), request.addressDetail(), request.coordinateX(), request.coordinateY());

        memberRepository.save(member);
    }
}
