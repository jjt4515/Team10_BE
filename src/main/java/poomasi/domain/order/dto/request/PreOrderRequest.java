package poomasi.domain.order.dto.request;

import java.util.List;

public record PreOrderRequest(
        List<ProductOrderRequest> productOrderRequest,
        String destinationAddress,
        String destinationAddressDetail,
        String deliveryRequest)
{
    public PreOrderRequest {
        if (deliveryRequest == null) {
            deliveryRequest = "조심히 다뤄 주세요";
        }
    }
}
