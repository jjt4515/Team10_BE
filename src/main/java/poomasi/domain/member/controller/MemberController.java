package poomasi.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.auth.token.whitelist.service.WhitelistRedisService;
import poomasi.domain.member.dto.response.*;
import poomasi.domain.member._profile.dto.request.AddressUpdateRequest;
import poomasi.domain.member.dto.request.CustomerUpdateRequest;
import poomasi.domain.member.dto.request.FarmerUpdateRequest;
import poomasi.domain.member.dto.request.SignupRequest;
import poomasi.domain.member.dto.response.FarmerResponse;
import poomasi.domain.member.dto.response.MemberResponse;
import poomasi.domain.member.dto.response.MemberSummaryResponse;
import poomasi.domain.member.dto.response.SignUpResponse;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.service.MemberService;
import poomasi.domain.member.dto.request.SignupRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok(memberService
                .signUp(signupRequest));
    }

    @GetMapping("/{memberId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable Long memberId) {
        MemberResponse memberResponse = memberService.getMemberById(memberId);
        return ResponseEntity.ok(memberResponse);
    }

    @GetMapping("/summary")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Page<MemberSummaryResponse>> getMembersSummary(@PageableDefault(size = 10) Pageable pageable) {
        Page<MemberSummaryResponse> memberSummaryResponses = memberService.getAllMembersSummary(pageable);
        return ResponseEntity.ok(memberSummaryResponses);
    }

    @GetMapping("/self")
    @Secured({"ROLE_CUSTOMER", "ROLE_FARMER", "ROLE_ADMIN"})
    public ResponseEntity<MemberResponse> getSelfMember(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        MemberResponse memberResponse = memberService.getMemberById(member.getId());
        return ResponseEntity.ok(memberResponse);
    }

    @GetMapping("/summary/{memberId}")
    public ResponseEntity<MemberSummaryResponse> getMemberSummaryById(@PathVariable Long memberId) {
        MemberSummaryResponse memberSummaryResponse = memberService.getMemberSummary(memberId);
        return ResponseEntity.ok(memberSummaryResponse);
    }

    // 회원 탈퇴
    @DeleteMapping("/delete")
    @Secured({"ROLE_CUSTOMER", "ROLE_FARMER", "ROLE_ADMIN"})
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        memberService.deleteAccount(member);
        return ResponseEntity.noContent().build();
    }

    // 계정 복구
    @PutMapping("/restore/{memberId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> restoreAccount(@PathVariable Long memberId) {
        memberService.restoreAccount(memberId);
        return ResponseEntity.ok().build();
    }

    // 계정 정지
    @PutMapping("/suspend/{memberId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> suspendAccount(@PathVariable Long memberId) {
        memberService.suspendAccount(memberId);
        return ResponseEntity.ok().build();
    }
}
