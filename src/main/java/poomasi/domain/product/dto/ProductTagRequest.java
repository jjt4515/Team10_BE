package poomasi.domain.product.dto;

public record ProductTagRequest(
        Long productId,
        String tagEnum
) {
}
