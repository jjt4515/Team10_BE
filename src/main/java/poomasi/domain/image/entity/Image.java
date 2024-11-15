package poomasi.domain.image.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import poomasi.domain.image.dto.request.ImageRequest;
import poomasi.domain.image.dto.response.ImageResponse;

import java.time.LocalDateTime;

@Entity
@Table(name = "image")
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE image SET deleted_at = current_timestamp WHERE id = ?")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String objectKey;

    @Column(nullable = false, unique = true)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType type;

    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Image(String objectKey, String imageUrl, ImageType type, Long referenceId) {
        this.objectKey = objectKey;
        this.imageUrl = imageUrl;
        this.type = type;
        this.referenceId = referenceId;
    }

    @Builder
    public Image(Long id, String objectKey, String imageUrl, ImageType type, Long referenceId, LocalDateTime createdAt, LocalDateTime deletedAt) {
        this.id = id;
        this.objectKey = objectKey;
        this.imageUrl = imageUrl;
        this.type = type;
        this.referenceId = referenceId;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }


    public void update(ImageRequest request) {
        this.objectKey = request.objectKey();
        this.imageUrl = request.imageUrl();
        this.type = request.type();
        this.referenceId = request.referenceId();
    }

    public ImageRequest toRequest(Image image){
        return new ImageRequest(
                image.objectKey,
                image.imageUrl,
                image.type,
                image.referenceId
        );
    }

    public static Image fromResponse(ImageResponse imageResponse) {
        return new Image(
                imageResponse.id(),
                imageResponse.objectKey(),
                imageResponse.imageUrl(),
                imageResponse.type(),
                imageResponse.referenceId(),
                imageResponse.createdAt(),
                imageResponse.deletedAt()
        );
    }
}