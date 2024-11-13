package poomasi.domain.farm._category.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import org.hibernate.annotations.Comment;

@Entity
@Getter
public class FarmCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("농장 카테고리 이름")
    private String name;

    @Comment("농장 카테고리 이미지 URL")
    private String imageUrl;
}
