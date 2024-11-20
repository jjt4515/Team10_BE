package poomasi.domain.statistics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.statistics.dto.response.StoreMonthlySalesResponse;
import poomasi.domain.order.entity.Order;
import poomasi.domain.order.entity.OrderedProduct;
import poomasi.domain.order.entity.OrderedProductStatus;
import poomasi.domain.order.service.OrderService;
import poomasi.domain.statistics.dto.response.CategoryMonthlySalesResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StatisticsService {
    private final OrderService orderService;

    private List<OrderedProduct> getDeliveredProducts(List<Order> orders) {
        return orders.stream()
                .flatMap(order -> order.getOrderedProducts().stream())
                .filter(orderedProduct -> orderedProduct.getOrderedProductStatus() == OrderedProductStatus.DELIVERED)
                .collect(Collectors.toList());
    }

    private LocalDateTime[] calculateDateRange(LocalDate startDate, int months) {
        LocalDate endDate = startDate.plusMonths(months);
        return new LocalDateTime[]{
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        };
    }

    public Page<StoreMonthlySalesResponse> getMonthlyStoreSales(Long storeId, String startMonth, String endMonth, Pageable pageable) {
        LocalDate start = LocalDate.parse(startMonth + "-01");
        LocalDate end = LocalDate.parse(endMonth + "-01").withDayOfMonth(LocalDate.parse(endMonth + "-01").lengthOfMonth());

        LocalDateTime startDate = start.atStartOfDay();
        LocalDateTime endDate = end.atTime(23, 59, 59);

        List<Order> orders = orderService.getOrdersByUpdateAtBetween(startDate, endDate);
        List<OrderedProduct> deliveredProducts = getDeliveredProducts(orders);

        // 월별 매출 계산
        List<StoreMonthlySalesResponse> monthlySales = deliveredProducts.stream()
                .filter(orderedProduct -> orderedProduct.getStoreId().equals(storeId))
                .collect(Collectors.groupingBy(OrderedProduct::getUpdateMonth))
                .entrySet().stream()
                .map(entry -> {
                    BigDecimal totalSales = entry.getValue().stream()
                            .map(orderedProduct -> orderedProduct.getPrice().multiply(BigDecimal.valueOf(orderedProduct.getCount())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new StoreMonthlySalesResponse(storeId, entry.getKey().name(), totalSales);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(monthlySales, pageable, monthlySales.size());
    }

    public Page<CategoryMonthlySalesResponse> getSixMonthCategorySales(Long storeId, LocalDate startDate, Pageable pageable) {
        LocalDateTime[] dateRange = calculateDateRange(startDate, 5);
        List<Order> orders = orderService.getOrdersByUpdateAtBetween(dateRange[0], dateRange[1]);
        List<OrderedProduct> deliveredProducts = getDeliveredProducts(orders);

        List<CategoryMonthlySalesResponse> categorySales = deliveredProducts.stream()
                .filter(orderedProduct -> orderedProduct.getStoreId().equals(storeId))
                .collect(Collectors.groupingBy(OrderedProduct::getCategoryId))
                .entrySet().stream()
                .map(entry -> {
                    BigDecimal totalSales = entry.getValue().stream()
                            .map(orderedProduct -> orderedProduct.getPrice().multiply(BigDecimal.valueOf(orderedProduct.getCount())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new CategoryMonthlySalesResponse(storeId, entry.getKey(), totalSales);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(categorySales, pageable, categorySales.size());
    }

}