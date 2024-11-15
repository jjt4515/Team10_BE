package poomasi.payment.controller;

import com.siot.IamportRestClient.exception.IamportResponseException;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import poomasi.payment.service.PaymentPortoneService;
import poomasi.payment.dto.request.PaymentWebHookRequest;
import poomasi.payment.service.PaymentPortoneService;

import java.io.IOException;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentPortoneService paymentService;

    @Description("포트원 웹훅 수신 api")
    @PostMapping("/portone-webhook")
    public void handleIamportWebhook(@RequestBody PaymentWebHookRequest paymentWebHookRequest)
            throws IamportResponseException, IOException {
        paymentService.handlePortOneProductWebhookEvent(paymentWebHookRequest);
    }

    //TODO : 프론트엔드에게 promise로 받는 것
    //여기서 SQS 처리한다.
}

