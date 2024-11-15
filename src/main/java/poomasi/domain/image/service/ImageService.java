package poomasi.domain.image.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.image.dto.ImageRequest;
import poomasi.domain.image.entity.Image;
import poomasi.domain.image.entity.ImageType;
import poomasi.domain.image.repository.ImageRepository;
import poomasi.domain.image.validation.ImageOwnerValidator;
import poomasi.domain.image.validation.ImageOwnerValidatorFactory;
import poomasi.domain.member._profile.entity.MemberProfile;
import poomasi.domain.member._profile.service.MemberProfileService;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.repository.MemberRepository;
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

    private static final int DEFAULT_IMAGE_LIMIT = 5;
    private static final int MEMBER_PROFILE_IMAGE_LIMIT = 1;

    private final ImageRepository imageRepository;
    private final ImageOwnerValidatorFactory validatorFactory;
    private final MemberRepository memberRepository;
    private final MemberProfileService memberProfileService;


    @Transactional
    public Image saveImage(Long memberId, ImageRequest imageRequest) {
        // 기존 이미지가 있는 경우 복구 또는 예외 처리 (실제 복구 로직과는 차이가 있음)
        validateImageOwner(memberId, imageRequest.type(), imageRequest.referenceId());
        validateImageLimit(imageRequest);

        Image image = findExistingOrRecoverableImage(imageRequest)
                .map(existingImage -> recoverImageOrThrow(existingImage, imageRequest))
                .orElseGet(() -> imageRequest.toEntity(imageRequest));

        if (imageRequest.type() == ImageType.MEMBER_PROFILE) {
            linkImageToMemberProfile(imageRequest.referenceId(), image);
        }

        return imageRepository.save(image);
    }

    // 이미지 주인이 맞는지 검증
    private void validateImageOwner(Long memberId, ImageType type, Long referenceId) {
        // 관리자는 제외
        if (isAdmin(memberId)) {
            return;
        }
        ImageOwnerValidator validator = validatorFactory.getValidator(type);
        if (validator != null && !validator.validateOwner(memberId, referenceId)) {
            throw new BusinessException(IMAGE_OWNER_MISMATCH);
        }
    }

    private boolean isAdmin(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
        return member.isAdmin();
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
        int imageLimit = DEFAULT_IMAGE_LIMIT;
        if (imageRequest.type() == ImageType.MEMBER_PROFILE) {
            imageLimit = MEMBER_PROFILE_IMAGE_LIMIT; // 멤버 프로필 이미지는 한 장으로 제한
        }

        if (imageRepository.countByTypeAndReferenceIdAndDeletedAtIsNull(imageRequest.type(), imageRequest.referenceId()) >= imageLimit) {
            throw new BusinessException(IMAGE_LIMIT_EXCEED);
        }
    }

    private void linkImageToMemberProfile(Long referenceId, Image savedImage) {
        MemberProfile memberProfile = memberProfileService.getMemberProfileById(referenceId);
        memberProfile.setProfileImage(savedImage);
        memberProfileService.saveMemberProfile(memberProfile);
    }

    // 여러 이미지 저장
    @Transactional
    public List<Image> saveMultipleImages(Long memberId, List<ImageRequest> imageRequests) {
        return imageRequests.stream()
                .map(imageRequest -> saveImage(memberId, imageRequest))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteImage(Long memberId, Long id) {
        Image image = getImageById(id);
        validateImageOwner(memberId, image.getType(), image.getReferenceId());
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
    public Image updateImage(Long memberId, Long id, ImageRequest imageRequest) {
        Image image = getImageById(id);
        validateImageOwner(memberId, image.getType(), image.getReferenceId());

        if (!image.getType().equals(imageRequest.type()) ||
                !image.getReferenceId().equals(imageRequest.referenceId())) {
            validateImageLimit(imageRequest);
        }

        image.update(imageRequest);

        return imageRepository.save(image);
    }

    @Transactional
    public void recoverImage(Long memberId, Long id) {
        Image image = getImageById(id);
        validateImageOwner(memberId, image.getType(), image.getReferenceId());

        if (image.getDeletedAt() == null) {
            throw new BusinessException(IMAGE_ALREADY_EXISTS);
        }

        validateImageLimit(image.toRequest(image));

        image.setDeletedAt(null);
        imageRepository.save(image);
    }

}