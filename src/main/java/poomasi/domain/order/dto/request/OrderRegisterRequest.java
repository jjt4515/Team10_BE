package poomasi.domain.order.dto.request;

public record OrderRegisterRequest(String address,
                                   String addressDetails,
                                   String deliveryRequest) {
}
