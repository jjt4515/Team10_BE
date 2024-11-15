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
import poomasi.global.sqs.SQSRequest;
import poomasi.global.sqs.SQSService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poomasi.payment.service.PaymentPortoneService;
import poomasi.payment.dto.request.PaymentWebHookRequest;
import poomasi.payment.service.PaymentPortoneService;

import java.io.IOException;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentPortoneService paymentService;

    private final SQSService sqsService;

    @Description("포트원 웹훅 수신 api")
    @PostMapping("/portone-webhook")
    public void handleIamportWebhook(@RequestBody PaymentWebHookRequest paymentWebHookRequest)
            throws IamportResponseException, IOException {
        paymentService.handlePortOneProductWebhookEvent(paymentWebHookRequest);
    }

    @Description("포트원 웹훅 수신 대기 api")
    @PostMapping("/receive-sqs")
    public void receiveSqsMessagesqs(@RequestBody SQSRequest sqsRequest) throws InterruptedException {
        System.out.println("프론트엔드에서 SQS 요청을 수신했습니다.");
        }

}


