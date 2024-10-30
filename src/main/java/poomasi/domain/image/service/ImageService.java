package poomasi.domain.image.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.image.dto.ImageRequest;
import poomasi.domain.image.entity.Image;
import poomasi.domain.image.entity.ImageType;
import poomasi.domain.image.repository.ImageRepository;
import poomasi.global.error.BusinessException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static poomasi.global.error.BusinessError.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {
    private final ImageRepository imageRepository;

    @Transactional
    public Image saveImage(ImageRequest imageRequest) {
        // 기존 이미지가 있는 경우 복구 또는 예외 처리 (실제 복구 로직과는 차이가 있음)
        validateImageLimit(imageRequest);

        Image image = findExistingOrRecoverableImage(imageRequest)
                .map(existingImage -> recoverImageOrThrow(existingImage, imageRequest))
                .orElseGet(() -> imageRequest.toEntity(imageRequest));

        return imageRepository.save(image);
    }

    private Optional<Image> findExistingOrRecoverableImage(ImageRequest imageRequest) {
        return imageRepository.findByObjectKeyAndTypeAndReferenceId(
                imageRequest.objectKey(), imageRequest.type(), imageRequest.referenceId());
    }

    private Image recoverImageOrThrow(Image existingImage, ImageRequest imageRequest) {
        if (existingImage.getDeletedAt() == null) {
            throw new BusinessException(IMAGE_ALREADY_EXISTS);
        }
        existingImage.setDeletedAt(null);
        existingImage.setCreatedAt(new Date());
        existingImage.update(imageRequest);
        return existingImage;
    }

    private void validateImageLimit(ImageRequest imageRequest) {
        if (imageRepository.countByTypeAndReferenceIdAndDeletedAtIsNull(imageRequest.type(), imageRequest.referenceId()) >= 5) {
            throw new BusinessException(IMAGE_LIMIT_EXCEED);
        }
    }

    // 여러 이미지 저장
    @Transactional
    public List<Image> saveMultipleImages(List<ImageRequest> imageRequests) {
        return imageRequests.stream()
                .map(this::saveImage)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteImage(Long id) {
        imageRepository.deleteById(id);
    }

    public Image getImageById(Long id) {
        return imageRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(IMAGE_NOT_FOUND));
    }

    public List<Image> getImagesByTypeAndReferenceId(ImageType type, Long referenceId) {
        return imageRepository.findByTypeAndReferenceIdAndDeletedAtIsNull(type, referenceId);
    }

    // 이미지 수정
    @Transactional
    public Image updateImage(Long id, ImageRequest imageRequest) {
        Image image = getImageById(id);

        if (!image.getType().equals(imageRequest.type()) ||
                !image.getReferenceId().equals(imageRequest.referenceId())) {
            validateImageLimit(imageRequest);
        }

        image.update(imageRequest);

        return imageRepository.save(image);
    }

    @Transactional
    public void recoverImage(Long id) {
        Image image = getImageById(id);

        if (image.getDeletedAt() == null) {
            throw new BusinessException(IMAGE_ALREADY_EXISTS);
        }

        validateImageLimit(image.toRequest(image));

        image.setDeletedAt(null);
        imageRepository.save(image);
    }
}