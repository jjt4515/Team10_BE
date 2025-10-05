package poomasi.domain.order.controller;


import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.order.dto.request.PreOrderRequest;
import poomasi.domain.order.dto.request.RegisterInvoiceRequest;
import poomasi.domain.order.dto.response.OrderResponse;
import poomasi.domain.order.dto.response.PreOrderResponse;
import poomasi.domain.order.dto.response.RegisterInvoiceResponse;
import poomasi.domain.order.service.OrderService;

import java.util.List;


@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("")
    @Secured({"ROLE_CUSTOMER", "ROLE_FARMER"})
    public ResponseEntity<?> getAllOrders() {
        List<OrderResponse> orders = orderService.getOrders();
        return ResponseEntity.ok(orders);
    }

    @Secured({"ROLE_CUSTOMER", "ROLE_FARMER"})
    @PostMapping("/pre-order")
    @Description("product 사전 주문 등록")
    public ResponseEntity<?> createProductPreOrder(@RequestBody PreOrderRequest preOrderRequest) {
        PreOrderResponse preOrderResponse = orderService.productPreOrderRegister(preOrderRequest);

        return ResponseEntity.ok(preOrderResponse);
    }

    @Secured({"ROLE_FARMER"})
    @PostMapping("/register-invoice")
    @Description("운송장 번호 등록")
    public ResponseEntity<?> registerInvoice(@RequestBody RegisterInvoiceRequest registerInvoiceRequest){
        RegisterInvoiceResponse registerInvoiceResponse = orderService.registerInvoice(registerInvoiceRequest);
        return ResponseEntity.ok(registerInvoiceResponse);
    }

    @Secured({"ROLE_FARMER"})
    @GetMapping("/{storeId}")
    @Description("스토어 별 주문 조회")
    public ResponseEntity<?> getStoreOrders(@PathVariable Long storeId){
        return ResponseEntity.ok(
                orderService.getStoreOrders(storeId)
        );
    }
}




