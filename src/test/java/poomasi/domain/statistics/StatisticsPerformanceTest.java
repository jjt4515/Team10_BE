package poomasi.domain.statistics;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import poomasi.domain.statistics.dto.response.CategoryMonthlySalesResponse;
import poomasi.domain.statistics.dto.response.StoreMonthlySalesResponse;
import poomasi.domain.statistics.service.StatisticsService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class StatisticsPerformanceTest {

    @Autowired
    private StatisticsService statisticsService;

    @Test
    void measureMonthlyStoreSales() {
        log.info("\n========================================");
        log.info("월별 매장 매출 조회 성능 측정");
        log.info("========================================\n");

        log.info("JVM Warm-up 중...");
        for (int i = 0; i < 1; i++) {
            statisticsService.getMonthlyStoreSalesOptimized(
                    1L, "2024-01", "2024-06", PageRequest.of(0, 10)
            );
        }

        log.info("\n측정 시작...\n");
        List<Long> times = new ArrayList<>();
        Long[] storeIds = {1L, 2L, 3L, 4L, 5L};

        for (int i = 0; i < 5; i++) {
            long start = System.currentTimeMillis();

            Page<StoreMonthlySalesResponse> result = statisticsService.getMonthlyStoreSalesOptimized(
                    storeIds[i], "2024-01", "2024-06", PageRequest.of(0, 10)
            );

            long time = System.currentTimeMillis() - start;
            times.add(time);

            log.info("실행 {} (Store {}): {}ms | 결과: {}건",
                    String.format("%2d", i+1), storeIds[i], time, result.getContent().size());
        }

        // 통계 계산
        Collections.sort(times);
        double avg = times.stream().mapToLong(Long::longValue).average().orElse(0);
        long min = times.get(0);
        long max = times.get(times.size() - 1);
        long median = times.get(times.size() / 2);

        log.info("\n========================================");
        log.info("측정 결과");
        log.info("========================================");
        log.info("평균:   {}ms", avg);
        log.info("중앙값: {}ms", median);
        log.info("최소:   {}ms", min);
        log.info("최대:   {}ms", max);
        log.info("========================================");
        log.info("\n평균 응답시간 {}ms\n", avg);
    }

    @Test
    void measureCategorySales() {
        log.info("\n========================================");
        log.info("카테고리별 매출 조회 성능 측정");
        log.info("========================================\n");


        log.info("JVM Warm-up 중...");
        for (int i = 0; i < 1; i++) {
            statisticsService.getSixMonthCategorySalesOptimized(
                    1L, LocalDate.of(2024, 1, 1), PageRequest.of(0, 10)
            );
        }


        log.info("\n측정 시작...\n");
        List<Long> times = new ArrayList<>();
        Long[] storeIds = {1L, 2L, 3L, 4L, 5L};

        for (int i = 1; i < 2; i++) {
            long start = System.currentTimeMillis();

            Page<CategoryMonthlySalesResponse> result = statisticsService.getSixMonthCategorySalesOptimized(
                    storeIds[i], LocalDate.of(2024, 1, 1), PageRequest.of(0, 10)
            );

            long time = System.currentTimeMillis() - start;
            times.add(time);

            log.info("실행 {} (Store {}): {}ms | 결과: {}건",
                    String.format("%2d", i+1), storeIds[i], time, result.getContent().size());
        }

        // 통계 계산
        Collections.sort(times);
        double avg = times.stream().mapToLong(Long::longValue).average().orElse(0);
        long min = times.get(0);
        long max = times.get(times.size() - 1);
        long median = times.get(times.size() / 2);

        log.info("\n========================================");
        log.info("측정 결과");
        log.info("========================================");
        log.info("평균:   {}ms", avg);
        log.info("중앙값: {}ms", median);
        log.info("최소:   {}ms", min);
        log.info("최대:   {}ms", max);
        log.info("========================================");
        log.info("\n평균 응답시간 {}ms\n", avg);
    }

}