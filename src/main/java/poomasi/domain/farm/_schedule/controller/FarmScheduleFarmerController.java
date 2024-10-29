package poomasi.domain.farm._schedule.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.farm._schedule.dto.FarmScheduleUpdateRequest;
import poomasi.domain.farm._schedule.service.FarmScheduleService;
import poomasi.domain.member.entity.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/farm")
public class FarmScheduleFarmerController {

    private final FarmScheduleService farmScheduleService;

    @Secured("ROLE_FARMER")
    @PostMapping("/schedule")
    public ResponseEntity<?> addFarmSchedule(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody FarmScheduleUpdateRequest request) {
        Member member = userDetails.getMember();
        farmScheduleService.addFarmSchedule(request, member);
        return ResponseEntity.ok().build();
    }

}
