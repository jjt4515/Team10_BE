package poomasi.domain.reservation.controller;

import jdk.jfr.Description;
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

    @PostMapping("/pre-reservation")
    @Secured("ROLE_CUSTOMER")
    @Description("FARM 사전 주문")
    public ResponseEntity<?> createReservation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ReservationRequest request
    ) {
        Member member = userDetails.getMember();
        ReservationResponse reservation = reservationPlatformService.createReservation(member, request);

        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/get/{reservationId}")
    @Secured("ROLE_CUSTOMER")
    public ResponseEntity<?> getReservation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long reservationId
    ) {
        Member member = userDetails.getMember();
        ReservationResponse reservation = reservationPlatformService.getReservation(member, reservationId);
        return ResponseEntity.ok(reservation);
    }

    @PostMapping("/cancel/{reservationId}")
    @Secured("ROLE_CUSTOMER")
    public ResponseEntity<?> cancelReservation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long reservationId
    ) {
        Member member = userDetails.getMember();
        reservationPlatformService.cancelReservation(member, reservationId);
        return ResponseEntity.ok().build();
    }

}
