package poomasi.domain.admin.statistics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.admin.statistics.dto.response.StoreMonthlySalesResponse;
import poomasi.domain.order.entity.Order;
import poomasi.domain.order.entity.OrderedProduct;
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

    private StoreMonthlySalesResponse createStoreSalesResponse(OrderedProduct orderedProduct) {
        BigDecimal totalSales = orderedProduct.getPrice().multiply(BigDecimal.valueOf(orderedProduct.getCount()));
        return new StoreMonthlySalesResponse(orderedProduct.getStore(), totalSales);
    }

    private void updateCategorySales(Map<Category, CategoryMonthlySalesResponse> salesMap, OrderedProduct orderedProduct) {
        Product product = orderedProduct.getProduct();
        Category category = categoryService.getCategory(product.getCategoryId());

        CategoryMonthlySalesResponse salesResponse = salesMap.computeIfAbsent(category, k ->
                new CategoryMonthlySalesResponse(category.getName(), 0, BigDecimal.ZERO)
        );

        salesResponse.setCount(salesResponse.getCount() + 1);
        BigDecimal productTotal = product.getPrice().multiply(BigDecimal.valueOf(orderedProduct.getCount()));
        salesResponse.setTotalSales(salesResponse.getTotalSales().add(productTotal));
    }

    public Page<StoreMonthlySalesResponse> getMonthlyStoreSales(Long storeId, String startMonth, String endMonth, Pageable pageable) {
        LocalDate start = LocalDate.parse(startMonth + "-01");
        LocalDate end = LocalDate.parse(endMonth + "-01").withDayOfMonth(LocalDate.parse(endMonth + "-01").lengthOfMonth());

        LocalDateTime startDate = start.atStartOfDay();
        LocalDateTime endDate = end.atTime(23, 59, 59);

        List<Order> orders = orderService.getOrdersByUpdateAtBetween(startDate, endDate);

        List<OrderedProduct> deliveredProducts = getDeliveredProducts(orders);

        List<StoreMonthlySalesResponse> salesResponses = deliveredProducts.stream()
                .filter(orderedProduct -> orderedProduct.getStoreId().equals(storeId))
                .map(this::createStoreSalesResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(salesResponses, pageable, salesResponses.size());
    }

    public Page<CategoryMonthlySalesResponse> getCategoryMonthlySales(LocalDate startDate, Pageable pageable) {
        LocalDateTime[] dateRange = calculateDateRange(startDate, 5);
        List<Order> orders = orderService.getOrdersByUpdateAtBetween(dateRange[0], dateRange[1]);

        List<OrderedProduct> deliveredProducts = getDeliveredProducts(orders);

        Map<Category, CategoryMonthlySalesResponse> salesMap = new HashMap<>();

        deliveredProducts.forEach(orderedProduct -> updateCategorySales(salesMap, orderedProduct));

        List<CategoryMonthlySalesResponse> responseList = new ArrayList<>(salesMap.values());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), responseList.size());
        return new PageImpl<>(responseList.subList(start, end), pageable, responseList.size());
    }
}

