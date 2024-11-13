package poomasi.domain.farm.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;

@Entity
@Getter
public class FarmInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Comment("농장 ID")
    @Column(nullable = false)
    Long farmId;

    @Comment("이미지")
    @Column(nullable = false)
    String imageUrl;

    @Comment("설명")
    @Column(nullable = false)
    String description;

    @Comment("메인 이미지 여부")
    @Column(nullable = false)
    boolean isMain;

    @Builder
    public FarmInfo(Long farmId, String imageUrl, String description, boolean isMain) {
        this.farmId = farmId;
        this.imageUrl = imageUrl;
        this.description = description;
        this.isMain = isMain;
    }
}
