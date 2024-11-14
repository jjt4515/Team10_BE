package poomasi.domain.member._biz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poomasi.domain.member._biz.controller.BizProfileApproveRequest;
import poomasi.domain.member._biz.dto.request.BizProfileCreateRequest;
import poomasi.domain.member._biz.dto.response.BizProfileProfileResponse;
import poomasi.domain.member._biz.entity.MemberBizProfile;
import poomasi.domain.member.entity.Member;
import poomasi.global.ocr.OcrService;
import poomasi.global.ocr.dto.response.NaverOcrResponse;
import poomasi.global.ocr.dto.response.OcrResponse;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MemberBizProfileFarmerService {
    private final MemberBizProfileService memberBizProfileService;
    private final OcrService ocrService;

    public Long updateBizProfile(Member member, BizProfileCreateRequest request) {
        OcrResponse ocrResponse = ocrService.extractTextFromImage(ocrService.createRequest(request.imageUrl()));
        MemberBizProfile bizProfile = request.toEntity(member.getId(), false);

        if (ocrResponse instanceof NaverOcrResponse naverOcrResponse) {
            if (naverOcrResponse.getImages().isEmpty() || Objects.equals(naverOcrResponse.getImages().get(0).getInferResult(), "FAILURE")) {
                bizProfile.setNeedsAdminApproval(true);
            }
        }
        return memberBizProfileService.save(bizProfile).getId();
    }

    public Long approveBizProfile(BizProfileApproveRequest request) {
        MemberBizProfile bizProfile = memberBizProfileService.findByMemberId(request.memberId());
        bizProfile.setNeedsAdminApproval(false);
        return memberBizProfileService.save(bizProfile).getId();
    }

    public BizProfileProfileResponse getBizProfile(Member member) {
        return memberBizProfileService.findByMemberId(member.getId()).toResponse();
    }
}
