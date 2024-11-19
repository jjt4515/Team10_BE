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

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poomasi.payment.dto.request.PaymentValidateRequest;
import poomasi.payment.service.PaymentPortoneService;
import poomasi.payment.dto.request.PaymentWebHookRequest;
import poomasi.payment.service.PaymentPortoneService;

import java.io.IOException;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentPortoneService paymentService;


    @Description("포트원 웹훅 수신 api")
    @PostMapping("/portone-webhook")
    public void handleIamportWebhook(@RequestBody PaymentWebHookRequest paymentWebHookRequest) {
        paymentService.handlePortOneProductWebhookEvent(paymentWebHookRequest);
    }

    @Description("결제 마지막 확인 api")
    @GetMapping("/validate/{paymentsId}")
    public void validatePayment(@RequestBody PaymentValidateRequest PaymentValidateRequest){

    }

}


