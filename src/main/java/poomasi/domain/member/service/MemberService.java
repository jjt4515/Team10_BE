package poomasi.domain.member.service;

import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.member.dto.response.MemberResponse;
import poomasi.domain.member.dto.response.MemberSummaryResponse;
import poomasi.domain.member.entity.LoginType;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.repository.MemberRepository;
import poomasi.domain.member.dto.request.SignupRequest;
import poomasi.domain.member.dto.response.SignUpResponse;
import poomasi.global.error.BusinessException;

import static poomasi.domain.member.entity.Role.ROLE_CUSTOMER;
import static poomasi.domain.member.entity.Role.ROLE_FARMER;
import static poomasi.global.error.BusinessError.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Description("카카오톡으로 먼저 회원가입이 되어 있는 경우, 계정 연동을 진행합니다. ")
    @Transactional
    public SignUpResponse signUp(SignupRequest signupRequest) {
        String name = signupRequest.name();
        String email = signupRequest.email();
        String password = signupRequest.password();

        memberRepository.findByEmailAndDeletedAtIsNull(email)
                .ifPresent(member -> { throw new BusinessException(DUPLICATE_MEMBER_EMAIL); });

        Member newMember = new Member(name, email,
                passwordEncoder.encode(password),
                LoginType.LOCAL,
                ROLE_CUSTOMER);

        memberRepository.save(newMember);
        return new SignUpResponse(name, email, "회원 가입 성공");
    }

    public MemberResponse getMemberById(Long memberId) {
        Member member = findMemberById(memberId);
        return MemberResponse.fromEntity(member);
    }

    public MemberSummaryResponse getMemberSummary(Long memberId) {
        Member member = findMemberById(memberId);
        return MemberSummaryResponse.fromEntity(member);
    }

    public Page<MemberResponse> getAllMembers(Pageable pageable) {
        Page<Member> members = memberRepository.findAll(pageable);
        return members.map(MemberResponse::fromEntity);
    }

    @Transactional
    public void convertToFarmer(Member member, Boolean hasFarmerQualification) {
        if (member.isFarmer()) {
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

        if (member.isCustomer()) {
            throw new BusinessException(MEMBER_ALREADY_CUSTOMER);
        }

        member.setRole(ROLE_CUSTOMER);
        memberRepository.save(member);
    }

    public Member findMemberById(Long memberId) {
        return memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
    }

}
