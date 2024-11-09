package poomasi.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.dto.request.FarmerQualificationRequest;
import poomasi.domain.member.dto.response.MemberResponse;
import poomasi.domain.member.dto.response.MemberSummaryResponse;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.service.MemberService;
import poomasi.domain.member.dto.request.SignupRequest;
import poomasi.domain.member.dto.response.SignUpResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok(memberService
                .signUp(signupRequest));
    }

    @PutMapping("/toFarmer")
    @Secured("ROLE_CUSTOMER")
    public ResponseEntity<Void> convertToFarmer(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @RequestBody FarmerQualificationRequest request) {
        Member member = userDetails.getMember();
        memberService.convertToFarmer(member, request.hasFarmerQualification());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/toCustomer/{memberId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> convertToCustomer(@PathVariable Long memberId) {
        memberService.convertToCustomer(memberId);
        return ResponseEntity.noContent().build();
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
    @Secured({"ROLE_CUSTOMER", "ROLE_FARMER", "ROLE_ADMIN"})
    public ResponseEntity<MemberSummaryResponse> getMemberSummaryById(@PathVariable Long memberId) {
        MemberSummaryResponse memberSummaryResponse = memberService.getMemberSummary(memberId);
        return ResponseEntity.ok(memberSummaryResponse);
    }


}
