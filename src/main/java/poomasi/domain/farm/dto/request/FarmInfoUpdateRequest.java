package poomasi.domain.farm.dto.request;

public record FarmInfoUpdateRequest(
        Long farmId,
        String title,
        String content,
        String imageUrl
) {
}
