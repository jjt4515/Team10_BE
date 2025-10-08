package poomasi.domain.statistics.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.statistics.dto.response.CategoryMonthlySalesResponse;
import poomasi.domain.statistics.dto.response.StoreMonthlySalesResponse;
import poomasi.domain.statistics.service.StatisticsService;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/stores/{storeId}/monthly-sales")
    public ResponseEntity<Page<StoreMonthlySalesResponse>> getMonthlyStoreSales(
            @PathVariable Long storeId,
            @RequestParam String startMonth,
            @RequestParam String endMonth,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<StoreMonthlySalesResponse> salesResponses = statisticsService.getMonthlyStoreSalesOptimized(storeId, startMonth, endMonth, pageable);
        return ResponseEntity.ok(salesResponses);
    }

    @GetMapping("/stores/{storeId}/categories/six-month-sales")
    public ResponseEntity<Page<CategoryMonthlySalesResponse>> getSixMonthCategorySales(
            @PathVariable Long storeId,
            @RequestParam String startMonth,
            @PageableDefault(size = 10) Pageable pageable) {

        LocalDate startDate = LocalDate.parse(startMonth + "-01");
        Page<CategoryMonthlySalesResponse> salesResponses = statisticsService.getSixMonthCategorySalesOptimized(storeId, startDate, pageable);
        return ResponseEntity.ok(salesResponses);
    }
}
