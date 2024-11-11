package poomasi.domain.member._profile.entity;

import jakarta.persistence.*;
import lombok.*;
import poomasi.domain.image.entity.Image;
import poomasi.domain.member.entity.Member;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "member_profile")
@AllArgsConstructor
@Builder
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "profile_type")
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

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public MemberProfile() {
    }
}
