package poomasi.payment.dto.response;

import poomasi.payment.entity.Payment;
import poomasi.payment.entity.PaymentMethod;

import java.math.BigDecimal;

public record PaymentResponse(Long paymentId,
                              BigDecimal totalPrice,
                              BigDecimal discountPrice,
                              BigDecimal finalPrice,
                              PaymentMethod paymentMethod
) {
    public static PaymentResponse fromEntity(Payment payment){
        return new PaymentResponse(
                payment.getId(),
                payment.getTotalPrice(),
                payment.getDiscountPrice(),
                payment.getFinalPrice(),
                payment.getPaymentMethod()
        );
    }
}
