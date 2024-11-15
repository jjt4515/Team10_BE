package poomasi.domain.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.store.dto.StoreRegisterRequest;
import poomasi.domain.store.service.StoreService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {
    private final StoreService storeService;

    @Secured("ROLE_FARMER")
    @PostMapping("")
    public ResponseEntity<?> addStore(
            @RequestBody StoreRegisterRequest storeRegisterRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Member member = userDetails.getMember();
        storeService.addStore(storeRegisterRequest, member);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<?> getStore(@PathVariable Long memberId) {
        return ResponseEntity.ok(storeService.getStore(memberId));
    }

    @Secured("ROLE_FARMER")
    @PutMapping("")
    public ResponseEntity<?> updateStore(
            @RequestBody StoreRegisterRequest storeRegisterRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        storeService.updateStore(storeRegisterRequest, userDetails.getMember());
        return ResponseEntity.ok().build();
    }
}
