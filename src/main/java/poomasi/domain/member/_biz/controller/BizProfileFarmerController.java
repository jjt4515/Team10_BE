package poomasi.domain.member._biz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member._biz.dto.request.BizProfileCreateRequest;
import poomasi.domain.member._biz.service.MemberBizProfileFarmerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/biz/farmer")
public class BizProfileFarmerController {
    private final MemberBizProfileFarmerService memberBizProfileFarmerService;

    @PostMapping("/profile")
    @Secured("ROLE_FARMER")
    public ResponseEntity<?> saveBizProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid BizProfileCreateRequest request
    ) {
        return ResponseEntity.ok(memberBizProfileFarmerService.updateBizProfile(userDetails.getMember(), request));

    }

    @PostMapping("/profile/approve")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> approveBizProfile(
            @Valid BizProfileApproveRequest request
    ) {
        return ResponseEntity.ok(memberBizProfileFarmerService.approveBizProfile(request));
    }

}
