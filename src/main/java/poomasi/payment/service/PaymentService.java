package poomasi.payment.service;

import poomasi.payment.dto.request.PaymentPreRegisterRequest;
import poomasi.payment.dto.request.PaymentWebHookRequest;
import poomasi.payment.dto.response.PaymentPreRegisterResponse;
import poomasi.payment.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentPreRegisterResponse prepaymentRegister(PaymentPreRegisterRequest paymentPreRegisterRequest);

    void confirmBeforePayment(String impUid, String merchantUid);

    void handlePortOneProductWebhookEvent(PaymentWebHookRequest paymentWebHookRequest);
}
