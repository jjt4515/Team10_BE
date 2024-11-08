package poomasi.domain.order._payment.controller;

import com.siot.IamportRestClient.exception.IamportResponseException;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.order._payment.dto.request.PaymentPreRegisterRequest;
import poomasi.domain.order._payment.dto.request.PaymentWebHookRequest;
import poomasi.domain.order._payment.dto.response.PaymentResponse;
import poomasi.domain.order._payment.service.PaymentService;

import java.io.IOException;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Description("사전 결제 api")
    @Secured({"ROLE_CUSTOMER", "ROLE_FARMER"})
    @PostMapping("/pre-payment")
    public void postPrepare(PaymentPreRegisterRequest paymentPreRegisterRequest) throws IamportResponseException, IOException {
        paymentService.portonePrePaymentRegister(paymentPreRegisterRequest);
    }

    @Description("사후 결제(검증 api)")
    @PostMapping("/validate")
    public void validatePayment(PaymentWebHookRequest paymentWebHookRequest) throws IamportResponseException, IOException {
        paymentService.portoneVerifyPostPayment(paymentWebHookRequest);
    }

    /*
    *@Description("포트원 webhook + 동기화")
    *  */



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


}
