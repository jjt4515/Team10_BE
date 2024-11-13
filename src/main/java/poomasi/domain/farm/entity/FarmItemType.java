package poomasi.domain.farm.entity;

public enum FarmItemType {
    // 채소 종류
    LEAFY_VEGETABLE,    // 잎채소 (예: 상추, 배추)
    ROOT_VEGETABLE,     // 뿌리채소 (예: 무, 당근)
    FRUIT_VEGETABLE,    // 열매채소 (예: 토마토, 오이)

    // 곡물
    GRAIN,              // 곡물 (예: 쌀, 보리, 콩)

    // 과일 종류
    CITRUS_FRUIT,       // 감귤류 과일 (예: 유자, 한라봉)
    STONE_FRUIT,        // 핵과류 과일 (예: 복숭아, 자두)
    POME_FRUIT,         // 장과류 과일 (예: 사과, 배)
    BERRY,              // 베리류 과일 (예: 딸기, 블루베리)

    // 견과류 및 기타
    NUT,                // 견과류 (예: 밤, 은행)

    // 특산물 및 기타
    SPECIALTY           // 특산물 (예: 고추, 마늘, 녹차)
}
