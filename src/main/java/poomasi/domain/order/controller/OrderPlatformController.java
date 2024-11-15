package poomasi.domain.order.controller;

import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poomasi.domain.order.dto.request.PreOrderRequest;
import poomasi.domain.order.dto.response.PreOrderResponse;
import poomasi.domain.order.service.OrderService;
import poomasi.payment.service.PaymentPortoneService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderPlatformController {

    private final PaymentPortoneService paymentService;
    private final OrderService orderService;

    @Secured({"ROLE_CUSTOMER", "ROLE_FARMER"})
    @PostMapping("/pre-order")
    @Description("product 사전 주문 등록")
    public ResponseEntity<?> createProductPreOrder(@RequestBody PreOrderRequest preOrderRequest) {
        PreOrderResponse preOrderResponse = orderService.productPreOrderRegister(preOrderRequest);

        return ResponseEntity.ok(preOrderResponse);



        /*return ResponseEntity.ok(
                paymentService.prepaymentRegister(paymentPreRegisterRequest)
        );*/
    }
}
