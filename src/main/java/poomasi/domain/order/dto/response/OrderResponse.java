package poomasi.domain.order.dto.response;

import poomasi.domain.order.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record OrderResponse(Long orderId,
                            LocalDateTime createdAt,
                            BigDecimal totalAmount,
                            String address,
                            String addressDetail,
                            String deliveryRequest,
                            String paymentMethod,
                            List<OrderedProductResponse> orderedProductResponse) {
    public static OrderResponse fromEntity(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCreatedAt(),
                order.getTotalAmount(),
                order.getAddress(),
                order.getAddressDetail(),
                order.getDeliveryRequest(),
                order.getPaymentMethod(),
                order.getOrderedProducts()
                        .stream()
                        .map(OrderedProductResponse::fromEntity)
                        .collect(Collectors.toList())

        );
    }
}
