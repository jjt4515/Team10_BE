package poomasi.payment.controller;

import com.siot.IamportRestClient.exception.IamportResponseException;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import poomasi.payment.dto.response.PaymentResponse;
import poomasi.payment.service.PaymentPortoneService;
import poomasi.payment.dto.request.PaymentWebHookRequest;

import java.io.IOException;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentPortoneService paymentService;

    @Description("결제 바로 직전 포트원에서 보내는 confirm 요청" + " 결제를 진행하려면 HTTP Status 200 응답, 그렇지 않으면 500 응답 보내기")
    @PostMapping("/confirm")
    public ResponseEntity<?> ddd(@RequestParam String merchantUid, @RequestParam String impUid) {
        paymentService.confirmBeforePayment(merchantUid, impUid);
        return ResponseEntity.ok().build();
    }

    @Description("포트원 웹훅 수신 api")
    @PostMapping("/portone-webhook")
    public void handleIamportWebhook(@RequestBody PaymentWebHookRequest paymentWebHookRequest) throws IamportResponseException, IOException {
        paymentService.handlePortOneProductWebhookEvent(paymentWebHookRequest);
    }

}

