package poomasi.domain.order.service;

import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.order.dto.request.PreOrderRequest;
import poomasi.domain.order.dto.request.ProductOrderRequest;
import poomasi.domain.order.dto.response.OrderResponse;
import poomasi.domain.order.dto.response.PreOrderResponse;
import poomasi.domain.order.entity.Order;
import poomasi.domain.order.entity.OrderedProduct;
import poomasi.domain.order.entity.OrderedProductStatus;
import poomasi.domain.order.repository.OrderRepository;
import poomasi.domain.order.repository.OrderedProductRepository;
import poomasi.domain.product._cart.entity.Cart;
import poomasi.domain.product._cart.repository.CartRepository;
import poomasi.domain.product._cart.service.CartService;
import poomasi.domain.product.entity.Product;
import poomasi.domain.product.service.ProductService;
import poomasi.global.error.ApplicationException;
import poomasi.global.error.BusinessException;
import poomasi.payment.entity.ItemType;
import poomasi.payment.entity.Payment;
import poomasi.payment.util.PaymentUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static poomasi.domain.order.entity.OrderedProductStatus.DELIVERED;
import static poomasi.domain.order.entity.OrderedProductStatus.PENDING_SELLER_APPROVAL;
import static poomasi.global.error.ApplicationError.PAYMENT_NOT_FOUND;
import static poomasi.global.error.BusinessError.*;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    private final CartService cartService;
    private final ProductService productService;
    private final PaymentUtil paymentUtil;
    private final OrderedProductRepository orderedProductRepository;

    public List<OrderResponse> getOrders(int page, int size){
        Member member = getMember();
        Long memberId = member.getId();
        Page<Order> orders = orderRepository.findByMemberId(
                memberId, PageRequest.
                        of(page, size, Sort.by("createdAt")
                                .descending()
                        )
        );
        return orders.stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Description("사전 주문 생성 메서드")
    @Transactional
    public PreOrderResponse productPreOrderRegister(PreOrderRequest preOrderRequest) {

        Member member = getMember();
        Long memberId = member.getId();

        List<ProductOrderRequest> productOrderRequest = preOrderRequest.productOrderRequest();
        List<Cart> cartList = cartService.getCart(memberId);

        // 예외처리 : 요청의 productId와 cart의 productId를 뽑아내고 값을 비교한다.
        List<Long> productIdRequest = productOrderRequest.stream().map(ProductOrderRequest::productId).collect(Collectors.toList());
        List<Long> productIdInCart = cartService.extractProductIds(cartList);

        if (productIdRequest.size() != productIdInCart.size() || !productIdRequest.containsAll(productIdInCart)) {
            throw new BusinessException(CART_PRODUCT_MISMATCHING);
        }

        String destinationAddress = preOrderRequest.destinationAddress();
        String destinationAddressDetail = preOrderRequest.destinationAddressDetail();
        String deliveryRequest = preOrderRequest.deliveryRequest();

        // 검증 완료
        Order order = Order
                .builder()
                .member(member)
                .totalAmount(BigDecimal.ZERO)
                .address(destinationAddress)
                .addressDetail(destinationAddressDetail)
                .deliveryRequest(deliveryRequest)
                .build();

        orderRepository.save(order);

        for (ProductOrderRequest productOrderRequest1 : productOrderRequest) {
            Long productId = productOrderRequest1.productId();
            Product product = productService.findValidProductById(productId);
            Integer productStock = product.getStock();
            Integer quantityInOrderRequest = productOrderRequest1.count();
            // 1. 주문 상품이 재고보다 더 많으면
            if (quantityInOrderRequest > productStock) {
                throw new BusinessException(PRODUCT_STOCK_ZERO);
            }

            product.subtractStock(quantityInOrderRequest);

            // 2. 자신의 장바구니 아이템이 아니라면
            Cart cart = cartRepository.findByMemberIdAndProductId(member.getId(), productId)
                    .orElseThrow(()-> new BusinessException(CART_PRODUCT_MISMATCHING));

            cartRepository.delete(cart);

            // 3. 검증이 완료 되었다면 orderedProduct를 추가한다.
            String productDescription = product.getDescription();
            String productName = product.getName();
            BigDecimal price = product.getPrice(); //1개당 가격

            OrderedProduct orderedProduct = OrderedProduct
                    .builder()
                    .product(product)
                    .order(order)
                    .productDescription(productDescription)
                    .productName(productName)
                    .price(price)
                    .count(quantityInOrderRequest)
                    .deliveryFee(product.getShippingFee())
                    .growEnv(product.getGrowEnv())
                    .build();

            order.addTotalAmount(price.multiply(BigDecimal.valueOf(quantityInOrderRequest.longValue())));
            order.addOrderedProduct(orderedProduct);
        }

        String merchantUid = paymentUtil.createMerchantUid(ItemType.PRODUCT);
        order.setMerchantUid(merchantUid);


        Payment payment = Payment
                .builder()
                .totalAmount(order.getTotalAmount())
                .checkSum(order.getTotalAmount())
                .itemType(ItemType.PRODUCT)
                .build();

        order.setPayment(payment);

        orderRepository.save(order);

        paymentUtil.sendPrepareData(merchantUid, order.getTotalAmount());
        return new PreOrderResponse(order.getMerchantUid());
    }


    public Order findByMerchantUid(String merchantUid) {
        return orderRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new ApplicationException(PAYMENT_NOT_FOUND));
    }


    @Description("요청이 구매자 소유인지 확인하고 판매자 대기중인지 체크하는 메서드")
    public OrderedProduct validateProductCancelable(Long memberId, Long orderedProductId){

        OrderedProduct orderedProduct = orderedProductRepository.findById(orderedProductId)
                .orElseThrow(()-> new BusinessException(ORDERED_PRODUCT_NOT_FOUND));
        if(orderedProduct.getOrder().getMember().getId() != memberId){
            new BusinessException(ORDERED_PRODUCT_NOT_FOUND);
        }
        OrderedProductStatus orderedProductStatus = orderedProduct.getOrderedProductStatus();
        if(orderedProductStatus != PENDING_SELLER_APPROVAL){
            throw new BusinessException(SHIPPING_ALREADY_IN_PROGRESS);
        }
        return orderedProduct;
    }


    @Description("요청이 구매자 소유인지 확인하고 환불 가능한지 확인하는 메섣,")
    public OrderedProduct validateProductRefundable(Long memberId, Long orderedProductId){

        OrderedProduct orderedProduct = orderedProductRepository.findById(orderedProductId)
                .orElseThrow(()-> new BusinessException(ORDERED_PRODUCT_NOT_FOUND));

        if(orderedProduct.getOrder().getMember().getId() != memberId){
            new BusinessException(ORDERED_PRODUCT_NOT_FOUND);
        }
        OrderedProductStatus orderedProductStatus = orderedProduct.getOrderedProductStatus();
        if(orderedProductStatus != DELIVERED){
            throw new BusinessException(REFUND_BAD_REQUEST);
        }
        return orderedProduct;
    }




    @Description("security context에서 member 객체 가져오는 메서드")
    private Member getMember() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        Object impl = authentication.getPrincipal();
        Member member = ((UserDetailsImpl) impl).getMember();
        return member;
    }


}



