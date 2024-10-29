package poomasi.domain.image.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.image.dto.ImageRequest;
import poomasi.domain.image.entity.Image;
import poomasi.domain.image.entity.ImageType;
import poomasi.domain.image.repository.ImageRepository;
import poomasi.global.error.BusinessException;

import java.util.List;
import java.util.stream.Collectors;

import static poomasi.global.error.BusinessError.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {
    private final ImageRepository imageRepository;

    @Transactional
    public Image saveImage(ImageRequest imageRequest) {
        validateImageConstraints(imageRequest);

        Image imageEntity = imageRequest.toEntity(imageRequest);
        return imageRepository.save(imageEntity);
    }

    private void validateImageConstraints(ImageRequest imageRequest) {
        String objectKey = imageRequest.objectKey();
        ImageType imageType = imageRequest.type();
        Long referenceId = imageRequest.referenceId();

        if (imageRepository.countByTypeAndReferenceIdAndDeletedAtIsNull(imageType, referenceId) >= 5) {
            throw new BusinessException(IMAGE_LIMIT_EXCEED);
        }

        if (imageRepository.existsByObjectKeyAndTypeAndReferenceIdAndDeletedAtIsNull(objectKey, imageType, referenceId)) {
            throw new BusinessException(IMAGE_ALREADY_EXISTS);
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

        ImageType imageType = imageRequest.type();
        Long referenceId = imageRequest.referenceId();

        if (imageRepository.countByTypeAndReferenceIdAndDeletedAtIsNull(imageType, referenceId) >= 5 &&
                !image.getType().equals(imageType)) {
            throw new BusinessException(IMAGE_LIMIT_EXCEED);
        }

        image.update(imageRequest);

        return imageRepository.save(image);
    }
}