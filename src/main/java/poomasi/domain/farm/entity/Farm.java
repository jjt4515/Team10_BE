package poomasi.domain.farm.entity;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import poomasi.domain.farm.dto.request.FarmUpdateRequest;
import poomasi.domain.review.entity.Review;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "farm")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE farm SET deleted_at=current_timestamp WHERE id = ?")
public class Farm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Comment("농장 소유자 ID")
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Comment("사업자 등록 번호")
    private String businessNumber;

    @Comment("농장 간단 설명")
    private String description;

    @Comment("도로명 주소")
    @Column(nullable = false)
    private String address;

    @Comment("상세 주소")
    private String addressDetail;

    @Comment("위도")
    @Column(nullable = false)
    private Double latitude;

    @Comment("경도")
    @Column(nullable = false)
    private Double longitude;

    @Comment("농장 대표 이미지")
    @Column(nullable = false)
    private String mainImage;

    @Comment("재배 환경")
    @Column(nullable = false)
    private String growEnv;

    @Comment("농장 상태")
    @Enumerated(EnumType.STRING)
    private FarmStatus status = FarmStatus.OPEN;

    @Comment("카테고리 ID")
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Comment("체험 비용")
    @Column(nullable = false)
    private BigDecimal experiencePrice;

    @Comment("팀 최대 인원")
    @Column(nullable = false)
    private Integer maxCapacity;

    @Comment("동일 시간대 최대 예약 가능 팀 수")
    @Column(nullable = false)
    private Integer maxReservation;

    @Comment("삭제 일시")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "entityId")
    private List<Review> reviewList = new ArrayList<>();

    private double averageRating;

    @Builder
    public Farm(Long id, String name, Long ownerId, String address, String addressDetail, Double latitude, Double longitude, String description, int experiencePrice, Integer maxCapacity, Integer maxReservation, String businessNumber, LocalDateTime deletedAt, Long categoryId, String phoneNumber, String mainImage, String growEnv) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.address = address;
        this.addressDetail = addressDetail;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.experiencePrice = new BigDecimal(experiencePrice);
        this.maxCapacity = maxCapacity;
        this.maxReservation = maxReservation;
        this.businessNumber = businessNumber;
        this.deletedAt = deletedAt;
        this.categoryId = categoryId;
        this.phoneNumber = phoneNumber;
        averageRating = 0.0f;
        this.mainImage = mainImage;
        this.growEnv = growEnv;
    }

    public Farm updateFarm(FarmUpdateRequest farmUpdateRequest) {
        this.name = farmUpdateRequest.name();
        this.address = farmUpdateRequest.address();
        this.addressDetail = farmUpdateRequest.addressDetail();
        this.latitude = farmUpdateRequest.latitude();
        this.longitude = farmUpdateRequest.longitude();
        this.description = farmUpdateRequest.description();
        return this;
    }

    public void updateExpPrice(int expPrice) {
        this.experiencePrice = new BigDecimal(expPrice);
    }

    public void updateMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public void updateMaxReservation(Integer maxReservation) {
        this.maxReservation = maxReservation;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void addReview(Review review) {
        this.reviewList.add(review);
        this.averageRating = reviewList.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0f);
    }
}
