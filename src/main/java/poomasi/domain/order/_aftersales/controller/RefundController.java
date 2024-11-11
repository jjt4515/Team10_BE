package poomasi.domain.order._aftersales.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.order._aftersales.dto.FullRefundRequest;
import poomasi.domain.order._aftersales.dto.PartialRefundRequest;
import poomasi.domain.order._aftersales.service.RefundService;

@RestController
@RequestMapping("/api/refund")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;


    @Secured({"ROLE_CUSTOMER", "ROLE_FARMER"})
    @GetMapping("/{refundId}")
    public void getRefund(@PathVariable("refundId") Long refundId) {

    }

    @Secured({"ROLE_CUSTOMER", "ROLE_FARMER"})
    @PostMapping("/{orderProductDetailsId}")
    public void processFullRefund (@PathVariable("orderProductDetailsId") Long orderProductDetailsId,
                                   @RequestBody FullRefundRequest fullRefundRequest) {
        //TODO : order product details 내부 메서드 보고
        //TODO : 환불 가능하지 받아 와야 함
    }


    @Secured({"ROLE_CUSTOMER", "ROLE_FARMER"})
    @PostMapping("/api/refund/{orderProductDetailsId}")
    public void processPartialRefund (@PathVariable("orderProductDetailsId") Long orderProductDetailsId,
                                      @RequestBody PartialRefundRequest partialRefundRequest) {



    }
}
