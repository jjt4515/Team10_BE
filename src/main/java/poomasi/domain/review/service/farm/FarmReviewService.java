package poomasi.domain.review.service.farm;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.farm.entity.Farm;
import poomasi.domain.farm.service.FarmService;
import poomasi.domain.member.entity.Member;
import poomasi.domain.reservation.entity.Reservation;
import poomasi.domain.reservation.entity.ReservationStatus;
import poomasi.domain.reservation.service.ReservationService;
import poomasi.domain.review.dto.ReviewRequest;
import poomasi.domain.review.dto.ReviewResponse;
import poomasi.domain.review.entity.EntityType;
import poomasi.domain.review.entity.Review;
import poomasi.domain.review.repository.ReviewRepository;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

@Service
@RequiredArgsConstructor
public class FarmReviewService {

    private final ReviewRepository reviewRepository;
    private final FarmService farmService;
    private final ReservationService reservationService;

    public List<ReviewResponse> getFarmReview(Long farmId) {
        Farm farm = getFarmByFarmId(farmId); //상품이 존재하는지 체크

        return farm.getReviewList().stream()
                .map(ReviewResponse::fromEntity).toList();
    }

    @Transactional
    public Long registerFarmReview(Member member, Long reservationId, ReviewRequest reviewRequest) {
        // s3 이미지 저장하고 주소 받아와서 review에 추가해주기
        Reservation reservation = reservationService.getReservationById(reservationId);

        checkStatusAndDate(reservation);

        Review review = reviewRequest.toEntity(reservation.getFarm().getId(), EntityType.FARM,
                member);
        review = reviewRepository.save(review);

        reservation.getFarm().addReview(review);
        reservation.setReview(review);

        return review.getId();
    }

    private void checkStatusAndDate(Reservation reservation) {
        if (reservation.getStatus() != ReservationStatus.DONE) {
            throw new BusinessException(BusinessError.RESERVATION_NOT_DONE);
        }

        if (reservation.getScheduleId().getDate().isBefore(LocalDate.now())) {
            throw new BusinessException(BusinessError.DATE_BEFORE_RESERVATION);
        }
    }

    private Farm getFarmByFarmId(Long farmId) {
        return farmService.getFarmByFarmId(farmId);
    }
}
