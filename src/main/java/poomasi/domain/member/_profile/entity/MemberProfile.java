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

    @Column(nullable = true, length = 50)
    private String name;

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
        this.name = "UNKNOWN"; // name not null 조건 때문에 임시로 넣었습니다. nullable도 true로 넣었는데 안 되네요
    }

    public MemberProfile(String name){
        this.name = name;
    }
}
