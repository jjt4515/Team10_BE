package poomasi.domain.admin.statistics.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.admin.statistics.dto.response.CategoryMonthlySalesResponse;
import poomasi.domain.admin.statistics.dto.response.StoreMonthlySalesResponse;
import poomasi.domain.admin.statistics.service.StatisticsService;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/stores/{storeId}/monthly-sales")
    public ResponseEntity<Page<StoreMonthlySalesResponse>> getMonthlyStoreSales(
            @PathVariable Long storeId,
            @RequestParam String startMonth,
            @RequestParam String endMonth,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<StoreMonthlySalesResponse> salesResponses = statisticsService.getMonthlyStoreSales(storeId, startMonth, endMonth, pageable);
        return ResponseEntity.ok(salesResponses);
    }

    @GetMapping("/categories/monthly-sales")
    public ResponseEntity<Page<CategoryMonthlySalesResponse>> getCategoryMonthlySales(
            @RequestParam String startMonth,
            @PageableDefault(size = 10) Pageable pageable) {

        LocalDate startDate = LocalDate.parse(startMonth + "-01");
        Page<CategoryMonthlySalesResponse> salesResponses = statisticsService.getCategoryMonthlySales(startDate, pageable);
        return ResponseEntity.ok(salesResponses);
    }
}
