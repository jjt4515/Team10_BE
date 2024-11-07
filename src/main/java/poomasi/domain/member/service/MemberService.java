package poomasi.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.member.dto.response.MemberResponse;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.repository.MemberRepository;
import poomasi.global.error.BusinessException;

import static poomasi.domain.member.entity.Role.ROLE_CUSTOMER;
import static poomasi.domain.member.entity.Role.ROLE_FARMER;
import static poomasi.global.error.BusinessError.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse getMemberById(Long memberId) {
        Member member = findMemberById(memberId);
        return MemberResponse.fromEntity(member);
    }

    public Page<MemberResponse> getAllMembers(Pageable pageable) {
        Page<Member> members = memberRepository.findAll(pageable);
        return members.map(MemberResponse::fromEntity);
    }


    @Transactional
    public void convertToFarmer(Long memberId, Boolean hasFarmerQualification) {
        Member member = findMemberById(memberId);

        if (isFarmer(memberId)) {
            throw new BusinessException(MEMBER_ALREADY_FARMER);
        }

        if (!hasFarmerQualification) {
            throw new BusinessException(INVALID_FARMER_QUALIFICATION);
        }

        member.setRole(ROLE_FARMER);
        memberRepository.save(member);
    }

    @Transactional
    public void convertToCustomer(Long memberId) {
        Member member = findMemberById(memberId);

        if (isCustomer(memberId)) {
            throw new BusinessException(MEMBER_ALREADY_CUSTOMER);
        }

        member.setRole(ROLE_CUSTOMER);
        memberRepository.save(member);
    }

    public Member findMemberById(Long memberId) {
        return memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
    }

    public boolean isCustomer(Long memberId) {
        Member member = findMemberById(memberId);
        return member.isCustomer();
    }

    public boolean isFarmer(Long memberId) {
        Member member = findMemberById(memberId);
        return member.isFarmer();
    }

    public boolean isAdmin(Long memberId) {
        Member member = findMemberById(memberId);
        return member.isAdmin();
    }
}
