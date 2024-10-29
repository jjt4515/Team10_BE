package poomasi.domain.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poomasi.domain.image.entity.Image;
import poomasi.domain.image.entity.ImageType;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    long countByTypeAndReferenceIdAndDeletedAtIsNull(ImageType type, Long referenceId);
    boolean existsByObjectKeyAndReferenceIdAndDeletedAtIsNull(String objectKey, Long referenceId);
    List<Image> findByTypeAndReferenceIdAndDeletedAtIsNull(ImageType type, Long referenceId);
    Optional<Image> findByIdAndDeletedAtIsNull(Long id);
}