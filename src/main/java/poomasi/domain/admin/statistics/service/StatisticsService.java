package poomasi.domain.admin.statistics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.admin.statistics.dto.response.StoreMonthlySalesResponse;
import poomasi.domain.order.entity.Order;
import poomasi.domain.order.entity.OrderedProductStatus;
import poomasi.domain.order.service.OrderService;
import poomasi.domain.admin.statistics.dto.response.CategoryMonthlySalesResponse;
import poomasi.domain.product._category.entity.Category;
import poomasi.domain.product._category.service.CategoryService;
import poomasi.domain.product.entity.Product;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StatisticsService {
    private final OrderService orderService;
    private final CategoryService categoryService;

    public Page<StoreMonthlySalesResponse> getMonthlyStoreSales(Long storeId, String startMonth, String endMonth, Pageable pageable) {
        LocalDate start = LocalDate.parse(startMonth + "-01");
        LocalDate end = LocalDate.parse(endMonth + "-01");
        end = end.withDayOfMonth(end.lengthOfMonth());

        LocalDateTime startDate = start.atStartOfDay();
        LocalDateTime endDate = end.atTime(23, 59, 59);

        List<Order> orders = orderService.getOrdersByUpdateAtBetween(startDate, endDate);

        List<StoreMonthlySalesResponse> salesResponses = orders.stream()
                .flatMap(order -> order.getOrderedProducts().stream())
                .filter(orderedProduct -> orderedProduct.getStoreId()
                        .equals(storeId) && orderedProduct.getOrderedProductStatus() == OrderedProductStatus.DELIVERED)
                .map(orderedProduct -> {
                    BigDecimal totalSales = orderedProduct.getPrice().multiply(BigDecimal.valueOf(orderedProduct.getCount()));
                    return new StoreMonthlySalesResponse(orderedProduct.getStore(), totalSales);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(salesResponses, pageable, salesResponses.size());
    }

    public Page<CategoryMonthlySalesResponse> getCategoryMonthlySales(LocalDate startDate, Pageable pageable) {

        LocalDate endDate = startDate.plusMonths(5);

        List<Order> orders = orderService.getOrdersByUpdateAtBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        Map<Category, CategoryMonthlySalesResponse> salesMap = new HashMap<>();

        orders.stream()
                .flatMap(order -> order.getOrderedProducts().stream())
                .filter(orderedProduct -> orderedProduct.getOrderedProductStatus() == OrderedProductStatus.DELIVERED)
                .forEach(orderedProduct -> {
                    Product product = orderedProduct.getProduct();
                    Long categoryId = product.getCategoryId();
                    Category category = categoryService.getCategory(categoryId);

                    CategoryMonthlySalesResponse salesResponse = salesMap.computeIfAbsent(category, k ->
                            new CategoryMonthlySalesResponse(category.getName(), 0, BigDecimal.ZERO)
                    );

                    salesResponse.setCount(salesResponse.getCount() + 1);
                    BigDecimal productTotal = product.getPrice().multiply(BigDecimal.valueOf(orderedProduct.getCount()));
                    salesResponse.setTotalSales(salesResponse.getTotalSales().add(productTotal));
                });

        List<CategoryMonthlySalesResponse> responseList = new ArrayList<>(salesMap.values());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responseList.size());
        Page<CategoryMonthlySalesResponse> page = new PageImpl<>(responseList.subList(start, end), pageable, responseList.size());

        return page;
    }
}

