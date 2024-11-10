package poomasi.domain.member._profile.entity;

import jakarta.persistence.*;
import lombok.*;
import poomasi.domain.image.entity.Image;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "member_profile")
@AllArgsConstructor
@Builder
public class MemberProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = true, length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    @Builder.Default
    private boolean isBanned = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id")
    private Image profileImage;

    // 기본 배송지
    @Column(nullable = true, length = 255)
    private String defaultAddress;

    @Column(nullable = true, length = 255)
    private String addressDetail;

    @Column(nullable=true, length=255)
    private Long coordinateX;

    @Column(nullable=true, length=255)
    private Long coordinateY;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public MemberProfile() {
    }
}
