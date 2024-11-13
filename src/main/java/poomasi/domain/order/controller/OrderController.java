package poomasi.domain.order.controller;


import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.order.service.ProductOrderService;


@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
public class OrderController {
    private final ProductOrderService productOrderService;


    @Description("멤버의 결제 완료가 된 단건 주문 조회. 특정 건만 조회")
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getAllOrdersByMember(@PathVariable Long orderId) {
        return ResponseEntity.ok(
                productOrderService.findOrderByMemberId(orderId)
        );
    }

    @Description("멤버의 결제 완료가 된 전체 주문 목록 조회. 전체 주문 목록 조회")
    @GetMapping("/")
    public ResponseEntity<?> getOrdersByMember() {
        return ResponseEntity.ok(
                productOrderService.findAllOrdersByMemberId()
        );
    }

    @Description("어떤 주문 대한 디테일 조회. ex) 주소, 상세주소, 배송 요청 사항 등")
    @GetMapping("/{orderId}/details")
    public ResponseEntity<?> getOrderDetailsByMember(@PathVariable Long orderId) {
        return ResponseEntity.ok(
                productOrderService.findOrderDetailsByOrderId(orderId)
        );
    }


    @Description("어떤 주문에 대한 모든 품목 디테일 조회.ex) 품목 가격, 이름, 등등..")
    @GetMapping("/{orderId}/product/details")
    public ResponseEntity<?> getOrderProductDetailsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(
                productOrderService.findAllOrderProductDetails(orderId)
        );
    }

    @Description("어떤 주문에 대한 품목의 디테일 단건 조회. ex) 품목 가격, 이름, 등등..")
    @GetMapping("/{orderId}/product/details/{detailsId}")
    public ResponseEntity<?> getOrderProductDetailsByDetailsId(@PathVariable Long orderId, @PathVariable Long detailsId) {
        return ResponseEntity.ok(
                productOrderService.findOrderProductDetailsById(orderId, detailsId)
        );
    }

}




