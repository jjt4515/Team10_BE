package poomasi.domain.product.entity;

public enum ProductTagEnum {
    ORGANIC("유기농"),
    NonPesticide("무농약");

    private final String value;

    private ProductTagEnum(String value) {
        this.value = value;
    }

    public String getKoreanName() {
        return value;
    }
}
