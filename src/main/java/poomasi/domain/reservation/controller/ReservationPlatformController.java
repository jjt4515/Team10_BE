package poomasi.domain.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.reservation.dto.request.ReservationRequest;
import poomasi.domain.reservation.dto.response.ReservationResponse;
import poomasi.domain.reservation.service.ReservationPlatformService;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationPlatformController {
    private final ReservationPlatformService reservationPlatformService;

    @PostMapping("/create")
    @Secured("ROLE_CUSTOMER")
    public ResponseEntity<?> createReservation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ReservationRequest request
    ) {
        Member member = userDetails.getMember();
        ReservationResponse reservation = reservationPlatformService.createReservation(request);
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/get/{reservationId}")
    @Secured("ROLE_CUSTOMER")
    public ResponseEntity<?> getReservation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long reservationId
    ) {
        Member member = userDetails.getMember();
        ReservationResponse reservation = reservationPlatformService.getReservation(member.getId(), reservationId);
        return ResponseEntity.ok(reservation);
    }

    @PostMapping("/cancel/{reservationId}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long reservationId) {
        // FIXME: 로그인한 사용자의 ID를 가져오도록 수정
        Long memberId = 1L;

        reservationPlatformService.cancelReservation(memberId, reservationId);
        return ResponseEntity.ok().build();
    }

}
