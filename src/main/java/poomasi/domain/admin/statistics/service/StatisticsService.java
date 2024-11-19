package poomasi.domain.admin.statistics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.admin.statistics.dto.response.StoreMonthlySalesResponse;
import poomasi.domain.order.entity.Order;
import poomasi.domain.order.service.OrderService;
import poomasi.domain.admin.statistics.dto.response.CategoryMonthlySalesResponse;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StatisticsService {
    private final OrderService orderService;

    @Transactional(readOnly = true)
    public Page<StoreMonthlySalesResponse> getMonthlyStoreSales(Long storeId, String startMonth, String endMonth, Pageable pageable) {
        LocalDate start = LocalDate.parse(startMonth + "-01");
        LocalDate end = LocalDate.parse(endMonth + "-01");
        end = end.withDayOfMonth(end.lengthOfMonth());

        LocalDateTime startDate = start.atStartOfDay();
        LocalDateTime endDate = end.atTime(23, 59, 59);

        List<Order> orders = orderService.getOrdersByUpdateAtBetween(startDate, endDate);

        // 주문 목록에서 각 OrderedProduct를 순회하여 storeId와 일치하는 주문 품목의 매출을 계산
        List<StoreMonthlySalesResponse> salesResponses = orders.stream()
                .flatMap(order -> order.getOrderedProducts().stream())
                .filter(orderedProduct -> orderedProduct.getProduct().getStore().getId().equals(storeId))
                .map(orderedProduct -> {
                    BigDecimal totalSales = orderedProduct.getPrice().multiply(BigDecimal.valueOf(orderedProduct.getCount()));
                    return new StoreMonthlySalesResponse(orderedProduct.getProduct().getStore(), totalSales);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(salesResponses, pageable, salesResponses.size());
    }

    public Page<CategoryMonthlySalesResponse> getCategoryMonthlySales(Long categoryId, String startMonth, String endMonth, Pageable pageable) {
        LocalDate start = LocalDate.parse(startMonth + "-01");
        LocalDate end = LocalDate.parse(endMonth + "-01");
        end = end.withDayOfMonth(end.lengthOfMonth());

        return orderService.getCategoryMonthlySales(categoryId, start.atStartOfDay(), end.atTime(LocalTime.MAX), pageable);
    }
}

