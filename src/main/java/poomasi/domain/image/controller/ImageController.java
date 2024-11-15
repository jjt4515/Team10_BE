package poomasi.domain.image.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.image.dto.request.ImageRequest;
import poomasi.domain.image.dto.response.ImageResponse;
import poomasi.domain.image.entity.ImageType;
import poomasi.domain.image.service.ImageService;
import poomasi.domain.member.entity.Member;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {
    private final ImageService imageService;

    // 이미지 정보 저장
    @PostMapping
    @Secured({"ROLE_CUSTOMER", "ROLE_FARMER", "ROLE_ADMIN"})
    public ResponseEntity<?> saveImageInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @Valid @RequestBody ImageRequest imageRequest) {
        Member member = userDetails.getMember();
        ImageResponse imageResponse = imageService.saveImage(member.getId(), imageRequest);
        return ResponseEntity.ok(imageResponse);
    }

    // 여러 이미지 정보 저장
    @PostMapping("/multiple")
    @Secured({"ROLE_CUSTOMER", "ROLE_FARMER", "ROLE_ADMIN"})
    public ResponseEntity<List<ImageResponse>> saveMultipleImages(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @Valid @RequestBody List<ImageRequest> imageRequests) {
        Member member = userDetails.getMember();
        List<ImageResponse> imageResponses = imageService.saveMultipleImages(member.getId(), imageRequests);
        return ResponseEntity.ok(imageResponses);
    }

    // 특정 이미지 삭제
    @DeleteMapping("/delete/{id}")
    @Secured({"ROLE_CUSTOMER", "ROLE_FARMER", "ROLE_ADMIN"})
    public ResponseEntity<Void> deleteImage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @PathVariable Long id) {
        Member member = userDetails.getMember();
        imageService.deleteImage(member.getId(), id);
        return ResponseEntity.noContent().build();
    }

    // 특정 이미지 조회
    @GetMapping("/{id}")
    public ResponseEntity<ImageResponse> getImage(@PathVariable Long id) {
        return ResponseEntity.ok(imageService.getImageById(id));
    }

    // 모든 이미지 조회 (특정 referenceId에 따라)
    @GetMapping("/reference/{type}/{referenceId}")
    public ResponseEntity<List<ImageResponse>> getImagesByTypeAndReference(@PathVariable ImageType type,
                                                                   @PathVariable Long referenceId) {
        List<ImageResponse> images = imageService.getImagesByTypeAndReferenceId(type, referenceId);
        return ResponseEntity.ok(images);
    }

    // 이미지 정보 수정
    @PutMapping("/update/{id}")
    @Secured({"ROLE_CUSTOMER", "ROLE_FARMER", "ROLE_ADMIN"})
    public ResponseEntity<?> updateImageInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @PathVariable Long id,
                                             @Valid @RequestBody ImageRequest imageRequest) {
        Member member = userDetails.getMember();
        ImageResponse updatedImage = imageService.updateImage(member.getId(), id, imageRequest);
        return ResponseEntity.ok(updatedImage);
    }

    @PutMapping("/recover/{id}")
    @Secured({"ROLE_CUSTOMER", "ROLE_FARMER", "ROLE_ADMIN"})
    public ResponseEntity<Void> recoverImage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @PathVariable Long id) {
        Member member = userDetails.getMember();
        imageService.recoverImage(member.getId(), id);
        return ResponseEntity.noContent().build();
    }


}