package poomasi.domain.farm.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "farm_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE farm_info SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLSelect(sql = "SELECT * FROM farm_info WHERE deleted_at IS NULL")
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

    @Comment(value = "제목")
    String title;

    @Comment("설명")
    String content;

    @Comment("메인 이미지 여부")
    @Column(nullable = false)
    boolean isMain;

    @Comment("생성일")
    @Column(nullable = false)
    @CurrentTimestamp
    LocalDateTime createdAt;

    @Comment("삭제 일시")
    LocalDateTime deletedAt;

    @Builder
    public FarmInfo(Long farmId, String imageUrl, String title, String content, boolean isMain) {
        this.farmId = farmId;
        this.imageUrl = imageUrl;
        this.title = title;
        this.content = content;
        this.isMain = isMain;
    }

    public boolean isValid() {
        return imageUrl != null && content != null && deletedAt == null && title != null;
    }

    public boolean hasContent() {
        return content != null && !content.isBlank() && !content.isEmpty() &&
                title != null && !title.isBlank() && !title.isEmpty();
    }
}
