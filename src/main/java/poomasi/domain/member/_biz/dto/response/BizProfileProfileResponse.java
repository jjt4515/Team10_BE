package poomasi.domain.member._biz.dto.response;

import lombok.Builder;

@Builder
public record BizProfileProfileResponse(
        String number,
        String imageUrl
) {
}
