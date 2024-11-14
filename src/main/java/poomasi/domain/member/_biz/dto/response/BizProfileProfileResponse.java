package poomasi.domain.member._biz.dto.response;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
public record BizProfileProfileResponse(
        String number,
        String imageUrl
) {
}
