package poomasi.domain.order.dto.request;

import jdk.jfr.Description;

@Description("cart에서 상품 정보 넘어오는 정보")
public record CartOrderRequest(
        Long cartId,
        Integer count
) {

}
