package poomasi.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.member.dto.request.FarmerQualificationRequest;
import poomasi.domain.member.dto.response.MemberResponse;
import poomasi.domain.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/member")
public class MemberController {

    private final MemberService memberService;

    @PutMapping("/toFarmer/{memberId}")
    public ResponseEntity<Void> upgradeToFarmer(@PathVariable Long memberId,
                                                @RequestBody FarmerQualificationRequest request) {
        memberService.upgradeToFarmer(memberId, request.hasFarmerQualification());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable Long memberId) {
        MemberResponse memberResponse = memberService.getMemberById(memberId);
        return ResponseEntity.ok(memberResponse);
    }

    @GetMapping
    public ResponseEntity<Page<MemberResponse>> getMembers(@PageableDefault(size = 10) Pageable pageable) {
        Page<MemberResponse> memberResponses = memberService.getAllMembers(pageable);
        return ResponseEntity.ok(memberResponses);
    }


}
