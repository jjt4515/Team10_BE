package poomasi.domain.farm.dto.request;

public record FarmInfoUpdateRequest(
        Long id,
        String title,
        String content,
        String imageUrl
) {
}
