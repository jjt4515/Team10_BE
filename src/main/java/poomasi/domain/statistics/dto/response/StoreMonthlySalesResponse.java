package poomasi.domain.statistics.dto.response;

import java.math.BigDecimal;

public record StoreMonthlySalesResponse(
        Long storeId,
        String month,
        BigDecimal totalSales
) {}