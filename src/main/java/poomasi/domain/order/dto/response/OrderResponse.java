package poomasi.domain.order.dto.response;

import poomasi.domain.order._payment.dto.response.PaymentResponse;
import poomasi.domain.order.entity.Order;
import poomasi.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record OrderResponse(Long orderId,
                            String merchantUid,
                            LocalDateTime createdAt,
                            List<OrderProductDetailsResponse> orderProductDetailsResponseList) {
    public static OrderResponse fromEntity(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getMerchantUid(),
                order.getCreatedAt(),
                order.getOrderProductDetails()
                        .stream()
                        .map(OrderProductDetailsResponse::fromEntity)
                        .collect(Collectors.toList())
                );
    }
}
