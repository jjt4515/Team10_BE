package poomasi.domain.member._profile.entity;

import jakarta.persistence.*;
import lombok.*;
import poomasi.domain.image.entity.Image;
import poomasi.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Table(name = "member_profile")
@AllArgsConstructor
@Builder
public class MemberProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    // customer인 경우
    @Column(nullable = true, length = 255)
    private String address;

    @Column(nullable = true, length = 255)
    private String addressDetail;

    @Column(nullable=true, length=255)
    private Long coordinateX;

    @Column(nullable=true, length=255)
    private Long coordinateY;

    // farmer인 경우
    @ElementCollection
    @CollectionTable(name = "business_registration_numbers", joinColumns = @JoinColumn(name = "farmer_profile_id"))
    @Column(nullable = true, length=255)
    private List<String> businessRegistrationNumbers;

//    @Setter
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id", referencedColumnName = "id")
//    private Member member;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public MemberProfile() {
    }
}
