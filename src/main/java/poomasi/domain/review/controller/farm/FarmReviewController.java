package poomasi.domain.review.controller.farm;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.review.dto.ReviewRequest;
import poomasi.domain.review.dto.ReviewResponse;
import poomasi.domain.review.service.farm.FarmReviewService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FarmReviewController {

    private final FarmReviewService farmReviewService;

    @GetMapping("/api/farms/{farmId}/reviews")
    public ResponseEntity<?> getProductReviews(@PathVariable Long farmId) {
        List<ReviewResponse> response = farmReviewService.getFarmReview(farmId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/api/farms/{reservationId}/reviews")
    public ResponseEntity<?> registerProductReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long reservationId,
            @RequestBody ReviewRequest reviewRequest) {
        Member member = userDetails.getMember();
        Long reviewId = farmReviewService.registerFarmReview(
                member, reservationId, reviewRequest);
        return new ResponseEntity<>(reviewId, HttpStatus.CREATED);
    }
}
