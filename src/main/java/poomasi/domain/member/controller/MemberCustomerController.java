package poomasi.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member._profile.dto.request.AddressUpdateRequest;
import poomasi.domain.member.dto.request.CustomerUpdateRequest;
import poomasi.domain.member.dto.response.MemberResponse;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.service.MemberCustomerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberCustomerController {
    private final MemberCustomerService memberCustomerService;

    @PutMapping("/to-customer/{memberId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> convertToCustomer(@PathVariable Long memberId) {
        memberCustomerService.convertToCustomer(memberId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/customer")
    @Secured("ROLE_CUSTOMER")
    public ResponseEntity<MemberResponse> updateCustomer(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CustomerUpdateRequest customerUpdateRequest) {

        Member member = userDetails.getMember();
        Member updatedMember = memberCustomerService.updateCustomer(member, customerUpdateRequest);

        MemberResponse memberResponse = MemberResponse.fromEntity(updatedMember);
        return ResponseEntity.ok(memberResponse);
    }

    @PutMapping("/update/customer/address")
    @Secured("ROLE_CUSTOMER")
    public ResponseEntity<Void> updateAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody AddressUpdateRequest addressUpdateRequest
    ) {
        Member member = userDetails.getMember();
        memberCustomerService.updateAddress(member, addressUpdateRequest);
        return ResponseEntity.ok().build();
    }
}
