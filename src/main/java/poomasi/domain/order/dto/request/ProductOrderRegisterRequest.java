package poomasi.domain.order.dto.request;

import java.util.List;

public record ProductOrderRegisterRequest(
    List<CartOrderRequest> carts,
    String destinationAddress,
    String destinationAddressDetail,
    String deliveryRequest)
{
}
