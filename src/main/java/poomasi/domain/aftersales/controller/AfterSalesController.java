package poomasi.domain.aftersales.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poomasi.domain.aftersales.dto.cancel.request.FarmCancelRequest;
import poomasi.domain.aftersales.dto.cancel.request.ProductCancelRequest;
import poomasi.domain.aftersales.dto.refund.request.ProductRefundRequest;
import poomasi.domain.aftersales.dto.refund.request.ProductRefundRequestApprovalRequest;
import poomasi.domain.aftersales.service.FarmAfterSalesService;
import poomasi.domain.aftersales.service.ProductAfterSalesService;

@RestController
@RequestMapping("/api/aftersales")
@RequiredArgsConstructor
public class AfterSalesController {

    private final ProductAfterSalesService productAfterSalesService;
    private final FarmAfterSalesService farmAfterSalesService;

    @Secured({"ROLE_CUSTOMER", "ROLE_FARMER"})
    @PostMapping("/products/cancel")
    public ResponseEntity<?> productCancel(@RequestBody ProductCancelRequest productCancelRequest) {
        return ResponseEntity.ok(
                productAfterSalesService.cancel(productCancelRequest)
        );
    }

    @Secured({"ROLE_CUSTOMER", "ROLE_FARMER"})
    @PostMapping("/products/refund-request")
    public ResponseEntity<?> requestRefund(@RequestBody ProductRefundRequest productRefundRequest) {
        return ResponseEntity.ok(
                productAfterSalesService.
                        refund(productRefundRequest)
        );
    }

    @Secured({"ROLE_FARMER"})
    @PostMapping("/approve-products-refund")
    public ResponseEntity<?> approveRefund(@RequestBody ProductRefundRequestApprovalRequest productRefundRequestApprovalRequest) {
        return ResponseEntity.ok(
                productAfterSalesService.processRefundApproval(productRefundRequestApprovalRequest)
        );
    }

    @Secured({"ROLE_CUSTOMER", "ROLE_FARMER"})
    @PostMapping("/farms/cancel")
    public ResponseEntity<?> farmCancel(@RequestBody FarmCancelRequest farmCancelRequest){
        return ResponseEntity.ok(
                farmAfterSalesService.cancel(farmCancelRequest)
        );
    }




}
