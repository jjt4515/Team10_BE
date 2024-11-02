package poomasi.domain.order.dto.response;

import poomasi.domain.order.entity.OrderDetails;

public record OrderDetailsResponse(
        String address,
        String addressDetails,
        String deliveryRequest
) {
    public static OrderDetailsResponse fromEntity(OrderDetails orderDetails) {
        return new OrderDetailsResponse(
                orderDetails.getAddress(),
                orderDetails.getAddressDetail(),
                orderDetails.getDeliveryRequest()
        );
    }
}
