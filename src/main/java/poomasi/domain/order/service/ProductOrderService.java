package poomasi.domain.order.service;

import java.util.ArrayList;
import java.util.Date;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.order.dto.request.CartOrderRequest;
import poomasi.domain.order.dto.request.ProductOrderRegisterRequest;
import poomasi.domain.order.dto.response.OrderDetailsResponse;
import poomasi.domain.order.dto.response.OrderProductDetailsResponse;
import poomasi.domain.order.dto.response.OrderResponse;
import poomasi.domain.order.entity.PaymentStatus;
import poomasi.domain.order.entity._product.OrderedProduct;
import poomasi.domain.order.entity._product.OrderedProductStatus;
import poomasi.domain.order.entity._product.ProductOrder;
import poomasi.domain.order.entity._product.ProductOrderDetails;
import poomasi.domain.order.repository.OrderedProductRepository;
import poomasi.domain.order.repository.ProductOrderDetailsRepository;
import poomasi.domain.order.repository.ProductOrderRepository;
import poomasi.domain.product._cart.entity.Cart;
import poomasi.domain.product._cart.repository.CartRepository;
import poomasi.domain.product._cart.service.CartService;
import poomasi.domain.product.entity.Product;
import poomasi.domain.product.repository.ProductRepository;
import poomasi.global.error.ApplicationException;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;
import poomasi.payment.dto.request.PaymentPreRegisterRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import poomasi.payment.entity.Payment;

import static poomasi.global.error.ApplicationError.PAYMENT_NOT_FOUND;
import static poomasi.global.error.BusinessError.*;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProductOrderService {

    private final ProductOrderRepository productOrderRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final OrderedProductRepository orderedProductRepository;
    private final ProductOrderDetailsRepository productOrderDetailsRepository;

    @Transactional
    public PaymentPreRegisterRequest productPreOrderRegister(ProductOrderRegisterRequest productOrderRegisterRequest) {
        Member member = getMember();
        List<Long> idList = productOrderRegisterRequest.carts().stream().map(CartOrderRequest::cartId).toList();
        List<Cart> cartList = cartService.getCartsByIdList(idList);

        String destinationAddress = productOrderRegisterRequest.destinationAddress();
        String destinationAddressDetail = productOrderRegisterRequest.destinationAddressDetail();
        String deliveryRequest = productOrderRegisterRequest.deliveryRequest();

        ProductOrder productOrder = ProductOrder
                .builder()
                .merchantUid("p" + new Date().getTime())
                .payment(new Payment())
                .totalAmount(BigDecimal.ZERO)
                .member(member)
                .orderedProducts(new ArrayList<>())
                .build();
        productOrderRepository.save(productOrder);

        ProductOrderDetails productOrderDetails = ProductOrderDetails
                .builder()
                .productOrder(productOrder)
                .destinationAddress(destinationAddress)
                .destinationAddressDetail(destinationAddressDetail)
                .deliveryRequest(deliveryRequest)
                .build();

        productOrderDetailsRepository.save(productOrderDetails);

        productOrder.setProductOrderDetails(productOrderDetails);

        //cart에 있는 총 가격 계산하기
        BigDecimal totalPrice = BigDecimal.ZERO;

        // cart 돌면서 productOrder details 추가
        for (int i=0 ; i<cartList.size() ; i++) {
            Product product = cartList.get(i).getProduct();
            if(product == null)
                throw new BusinessException(PRODUCT_NOT_FOUND);
            //Cart cart = cartList.get(i);

            Integer productStock = product.getStock();
            Integer quantityInCart = productOrderRegisterRequest.carts().get(i).count();

            //현재 남아있는 재고보다 더 많이 요청하면
            //pending 상태로 저장이 안 됨.
            if (quantityInCart > productStock) {
                throw new BusinessException(PRODUCT_STOCK_ZERO);
            }

            String productDescription = product.getDescription();
            String productName = product.getName();
            BigDecimal price = product.getPrice().multiply(BigDecimal.valueOf((long) quantityInCart));

            //TODO : Store store = product.getStore();

            OrderedProduct orderedProduct = OrderedProduct
                    .builder()
                    .product(product)
                    .orderedProductStatus(OrderedProductStatus.PENDING_SELLER_APPROVAL)
                    .deliveryFee(product.getShippingFee())
                    .productOrder(productOrder)
                    //.store(store)
                    .productDescription(productDescription)
                    .productName(productName)
                    .price(price)
                    .count(quantityInCart)
                    .build();

            productOrder.addOrderedProduct(orderedProduct);
            totalPrice = totalPrice.add(price);
        }
        productOrder.setTotalAmount(totalPrice);
        productOrder.setCheckSum(totalPrice);
        productOrderRepository.save(productOrder);

        productOrder.setProductOrderDetails(productOrderDetails);

        String merchantUid = productOrder.getMerchantUid();
        return new PaymentPreRegisterRequest(merchantUid, totalPrice);
    }

    @Description("멤버 ID 기반으로 모든 order 다 들고 오는 메서드")
    public List<OrderResponse> findAllOrdersByMemberId() {
        Member member = getMember();
        Long memberId = member.getId();
        List<ProductOrder> productOrderList = productOrderRepository.findByMemberId(memberId);
        return productOrderList
                .stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList()
                );
    }

    @Description("멤버 id 기반으로 특정 orderId 들고오는 메서드")
    public OrderResponse findOrderByMemberId(Long orderId) {
        Member member = getMember();
        ProductOrder productOrder = productOrderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND));

        validateOrderOwnership(productOrder, member);
        return OrderResponse.fromEntity(productOrder);
    }


    @Description("orderId 기반으로 order details(주소, 상세주소, 배송 요청 사항 ..등) 들고오는 메서드")
    public OrderDetailsResponse findOrderDetailsByOrderId(Long orderId) {
        ProductOrder productOrder = productOrderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND));
        ProductOrderDetails productOrderDetails = productOrder.getProductOrderDetails();

        return OrderDetailsResponse.fromEntity(productOrderDetails);
    }


    @Description("orderId에 해당하는 order product details 가져오는 메서드")
    public List<OrderProductDetailsResponse> findAllOrderProductDetails(Long orderId) {
        Member member = getMember();
        ProductOrder productOrder = productOrderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND));
        validateOrderOwnership(productOrder, member);
        return productOrder.getOrderedProducts()
                .stream()
                .map(OrderProductDetailsResponse::fromEntity)
                .collect(Collectors.toList()
                );
    }


    @Description("orderId에 해당하는 order product Details의 단건 조회")
    public OrderProductDetailsResponse findOrderProductDetailsById(Long orderId, Long orderProductDetailsId) {
        Member member = getMember();
        OrderedProduct orderedProduct = orderedProductRepository.findById(orderProductDetailsId)
                .orElseThrow(() -> new BusinessException(ORDER_PRODUCT_DETAILS_NOT_FOUND));
        ProductOrder productOrder = orderedProduct.getProductOrder();

        // productOrder product details의 주인 productOrder 검사 그리고 , orderId의 주인 member 검사
        validateOrderProductDetailsByOrderId(productOrder, orderId);
        validateOrderOwnership(productOrder, member);

        return OrderProductDetailsResponse.fromEntity(orderedProduct);
    }


    @Description("member의 order인지 검사하는 메서드")
    private void validateOrderOwnership(ProductOrder productOrder, Member member) {
        if (!productOrder.getMember().getId().equals(member.getId())) {
            throw new BusinessException(ORDER_NOT_OWNED_EXCEPTION);
        }
    }

    @Description("orderId에 해당하는 productOrder Product Details인지 조회하는 메서드")
    private void validateOrderProductDetailsByOrderId(ProductOrder productOrder, Long orderId) {
        if (productOrder.getId() != orderId) {
            throw new BusinessException(ORDER_PRODUCT_DETAILS_NOT_OWNED_EXCEPTION);
        }
    }


    @Description("주문 상태를 변경하는 메서드")
    private void changeOrderStatus(Long orderId, PaymentStatus paymentStatus) {
        ProductOrder productOrder = productOrderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND));
        productOrder.setPaymentStatus(paymentStatus);
    }


    @Description("security context에서 member 객체 가져오는 메서드")
    private Member getMember() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        Object impl = authentication.getPrincipal();
        Member member = ((UserDetailsImpl) impl).getMember();
        return member;
    }

    public ProductOrder findByMerchantUid(String merchantUid) {
        return productOrderRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new ApplicationException(PAYMENT_NOT_FOUND));
    }
}


