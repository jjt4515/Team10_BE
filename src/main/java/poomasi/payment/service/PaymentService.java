package poomasi.payment.service;

import poomasi.payment.dto.request.PaymentWebHookRequest;

import java.math.BigDecimal;

public interface PaymentService {

    void prepaymentRegister(String merchantUid, BigDecimal amount);

    void confirmBeforePayment(String impUid, String merchantUid);

    void handlePortOneProductWebhookEvent(PaymentWebHookRequest paymentWebHookRequest);
}
