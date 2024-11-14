package poomasi.domain.member.service;

import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.member._profile.entity.MemberProfile;
import poomasi.domain.member.dto.response.MemberResponse;
import poomasi.domain.member.dto.response.MemberSummaryResponse;
import poomasi.domain.member.entity.LoginType;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.repository.MemberRepository;
import poomasi.domain.member.dto.request.SignupRequest;
import poomasi.domain.member.dto.response.SignUpResponse;
import poomasi.global.error.BusinessException;

import static poomasi.domain.member.entity.Role.ROLE_CUSTOMER;
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

    public Page<MemberSummaryResponse> getAllMembersSummary(Pageable pageable) {
        Page<Member> members = memberRepository.findAll(pageable);
        return members.map(MemberSummaryResponse::fromEntity);
    }

    public Member findMemberById(Long memberId) {
        return memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
    }

    public void updateCommonAttributes(Member member, String name, String email, String password, String phoneNumber) {
        if (name != null) member.setName(name);
        if (email != null) member.setEmail(email);
        if (password != null) member.setPassword(passwordEncoder.encode(password));

        MemberProfile profile = member.getOrCreateProfile();
        if (phoneNumber != null) {
            profile.setPhoneNumber(phoneNumber);
        }
    }

    @Transactional
    public void deleteAccount(Member member) {
        memberRepository.delete(member);
    }

    @Transactional
    public void restoreAccount(Long memberId) {
        Member member = findMemberById(memberId);
        member.setDeletedAt(null);
        memberRepository.save(member);
    }

    @Transactional
    public void suspendAccount(Long memberId) {
        Member member = findMemberById(memberId);
        member.setIsBanned(true);
        memberRepository.save(member);
    }

}
