package poomasi.domain.admin.statistics.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.admin.statistics.dto.response.CategoryMonthlySalesResponse;
import poomasi.domain.admin.statistics.dto.response.StoreMonthlySalesResponse;
import poomasi.domain.admin.statistics.service.StatisticsService;
import poomasi.domain.order.service.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final OrderService orderService;

    @GetMapping("/stores/{storeId}/monthly-sales")
    public ResponseEntity<Page<StoreMonthlySalesResponse>> getMonthlyStoreSales(
            @PathVariable Long storeId,
            @RequestParam String startMonth,
            @RequestParam String endMonth,
            Pageable pageable) {

        Page<StoreMonthlySalesResponse> salesResponses = statisticsService.getMonthlyStoreSales(storeId, startMonth, endMonth, pageable);
        return ResponseEntity.ok(salesResponses);
    }

    @GetMapping("/categories/monthly-sales")
    public ResponseEntity<Page<CategoryMonthlySalesResponse>> getCategoryMonthlySales(
            @RequestParam Long categoryId,
            @RequestParam String startMonth,
            @RequestParam String endMonth,
            Pageable pageable
    ) {
        Page<CategoryMonthlySalesResponse> sales = statisticsService.getCategoryMonthlySales(categoryId, startMonth, endMonth, pageable);
        return ResponseEntity.ok(sales);
    }
}
