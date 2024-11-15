package poomasi.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.dto.request.ConvertToFarmerRequest;
import poomasi.domain.member.dto.request.FarmerUpdateRequest;
import poomasi.domain.member.dto.response.FarmerResponse;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.service.MemberFarmerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberFarmerController {
    private final MemberFarmerService memberFarmerService;

    @PutMapping("/to-farmer")
    @Secured("ROLE_CUSTOMER")
    public ResponseEntity<Void> convertToFarmer(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @RequestBody ConvertToFarmerRequest convertToFarmerRequest) {
        Member member = userDetails.getMember();
        memberFarmerService.convertToFarmer(member, convertToFarmerRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/farmer")
    @Secured("ROLE_FARMER")
    public ResponseEntity<FarmerResponse> updateFarmer(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody FarmerUpdateRequest farmerUpdateRequest) {

        Member member = userDetails.getMember();
        Member updatedMember = memberFarmerService.updateFarmer(member, farmerUpdateRequest);

        FarmerResponse memberResponse = FarmerResponse.fromEntity(updatedMember);
        return ResponseEntity.ok(memberResponse);
    }
}
