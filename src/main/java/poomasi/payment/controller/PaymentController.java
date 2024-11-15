package poomasi.payment.controller;

import com.siot.IamportRestClient.exception.IamportResponseException;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poomasi.payment.service.PaymentPortoneService;
import poomasi.payment.dto.request.PaymentWebHookRequest;

import java.io.IOException;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentPortoneService paymentService;

    @Description("포트원 웹훅 수신 api")
    @PostMapping("/portone-webhook")
    public void handleIamportWebhook(@RequestBody PaymentWebHookRequest paymentWebHookRequest) throws IamportResponseException, IOException {
        paymentService.handlePortOneProductWebhookEvent(paymentWebHookRequest);
    }
}

    /*

    @GetMapping("/")
    @Secured("ROLE_CUSTOMER")
    @Description("결제 내역 단건 조회")
    public ResponseEntity<?> getPaymentById(Long paymentId){
        PaymentResponse paymentResponse =  paymentService.getPayment(paymentId);
        return ResponseEntity.ok(paymentResponse);
    }


    @Secured("ROLE_CUSTOMER")
    @Description("order에 해당하는 결제 내역 조회")
    public ResponseEntity<?> getPaymentByOrderId(@RequestParam Long orderId){
        PaymentResponse paymentResponse =  paymentService.getPayment(orderId);
        return ResponseEntity.ok(paymentResponse);
    }

    */


