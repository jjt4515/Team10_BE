package poomasi.domain.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.reservation.service.ReservationFarmerService;

@RequestMapping("/api/v1/farmer/reservations")
@RestController
@RequiredArgsConstructor
public class ReservationFarmerController {
    private final ReservationFarmerService reservationFarmerService;

    @GetMapping("")
    @Secured("ROLE_FARMER")
    public ResponseEntity<?> getReservations(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Member member = userDetails.getMember();
        return ResponseEntity.ok(reservationFarmerService.getReservationsByFarmerId(member));
    }
}
