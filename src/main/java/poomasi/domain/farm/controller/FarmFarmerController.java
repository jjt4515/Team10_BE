package poomasi.domain.farm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.farm.dto.request.FarmInfoRegisterRequest;
import poomasi.domain.farm.dto.request.FarmInfoUpdateRequest;
import poomasi.domain.farm.dto.request.FarmRegisterRequest;
import poomasi.domain.farm.dto.request.FarmUpdateRequest;
import poomasi.domain.farm.service.FarmFarmerService;
import poomasi.domain.farm._schedule.service.FarmScheduleService;
import poomasi.domain.member.entity.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/farm")
public class FarmFarmerController {
    private final FarmFarmerService farmFarmerService;
    private final FarmScheduleService farmScheduleService;


    @Secured("ROLE_FARMER")
    @PostMapping("")
    public ResponseEntity<?> registerFarm(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody FarmRegisterRequest request) {
        Member member = userDetails.getMember();
        return ResponseEntity.ok(farmFarmerService.registerFarm(member, request));

    }

    @Secured("ROLE_FARMER")
    @PostMapping("/info")
    public ResponseEntity<?> registerFarmInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody FarmInfoRegisterRequest request) {
        Member member = userDetails.getMember();
        return ResponseEntity.ok(farmFarmerService.registerFarmInfo(member, request));

    }

    @Secured("ROLE_FARMER")
    @PostMapping("/info/update")
    public ResponseEntity<?> updateFarmInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody FarmInfoUpdateRequest request) {
        Member member = userDetails.getMember();
        return ResponseEntity.ok(farmFarmerService.updateFarmInfo(member, request));
    }


    @Secured("ROLE_FARMER")
    @PostMapping("/update")
    public ResponseEntity<?> updateFarm(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody FarmUpdateRequest request) {
        Member member = userDetails.getMember();
        return ResponseEntity.ok(farmFarmerService.updateFarm(member.getId(), request));
    }

    @Secured("ROLE_FARMER")
    @DeleteMapping("/{farmId}")
    public ResponseEntity<?> deleteFarm(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long farmId) {
        Member member = userDetails.getMember();

        farmFarmerService.deleteFarm(member.getId(), farmId);
        return ResponseEntity.ok().build();
    }

}
