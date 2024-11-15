package poomasi.domain.member._biz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member._biz.dto.request.BizProfileCreateRequest;
import poomasi.domain.member._biz.service.MemberBizProfileFarmerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/farmer/biz-profile")
public class BizProfileFarmerController {
    private final MemberBizProfileFarmerService memberBizProfileFarmerService;

    @PostMapping
    @Secured("ROLE_FARMER")
    public ResponseEntity<?> saveBizProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody BizProfileCreateRequest request
    ) {
        return ResponseEntity.ok(memberBizProfileFarmerService.updateBizProfile(userDetails.getMember(), request));

    }

    @GetMapping
    @Secured("ROLE_FARMER")
    public ResponseEntity<?> getBizProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(memberBizProfileFarmerService.getBizProfile(userDetails.getMember()));
    }

    @PostMapping("/approve")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> approveBizProfile(
            @Valid @RequestBody BizProfileApproveRequest request
    ) {
        return ResponseEntity.ok(memberBizProfileFarmerService.approveBizProfile(request));
    }

}
